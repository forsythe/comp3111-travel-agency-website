package comp3111.input.editors;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import comp3111.Utils;
import comp3111.data.DBManager;
import comp3111.data.model.Customer;
import comp3111.data.model.Offering;
import comp3111.data.model.Tour;
import comp3111.data.model.TourGuide;
import comp3111.data.repo.OfferingRepository;
import comp3111.input.converters.LocalDateToUtilDateConverter;
import comp3111.input.converters.TourGuideIDConverter;
import comp3111.input.exceptions.OfferingDateUnsupportedException;
import comp3111.input.exceptions.OfferingDayOfWeekUnsupportedException;
import comp3111.input.exceptions.TourGuideUnavailableException;
import comp3111.input.validators.IntegerLowerBoundedByAnotherFieldValidator;
import comp3111.input.validators.ValidatorFactory;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class OfferingEditor {
	private static final Logger log = LoggerFactory.getLogger(OfferingEditor.class);

	private OfferingRepository offeringRepo;
	private final HashSet<Offering> offeringsCollectionCached = new HashSet<>();

	@Autowired
	private TourGuideIDConverter tourGuideIDConverter;

	@Autowired
	private DBManager actionManager;

	@SuppressWarnings("unchecked")
	@Autowired
	public OfferingEditor(OfferingRepository tr) {
		this.offeringRepo = tr;
	}

	Window getSubWindow(Tour hostTour, Offering offeringToSave, TourEditor tourEditor) {
		// Creating the confirm button
		Button confirm = new Button("Confirm");
		confirm.setId("confirm_offering");

		// Creating the fields
		Label tourName = new Label(hostTour.getTourName());
		ComboBox<TourGuide> tourGuide = new ComboBox<TourGuide>("Tour Guide");
		DateField startDate = new DateField("Start Date");
		TextField hotelName = new TextField("Hotel Name");
		TextField minCustomer = new TextField("Min number of customer");
		TextField maxCustomer = new TextField("Max number of customer");

		Window subWindow = new Window("Create new offering");

		FormLayout subContent = new FormLayout();
		subWindow.setWidth("800px");

		subWindow.setContent(subContent);
		subWindow.center();
		subWindow.setClosable(false);
		subWindow.setModal(true);
		subWindow.setResizable(false);
		subWindow.setDraggable(false);

		subContent.addComponent(tourName);
		subContent.addComponent(tourGuide);
		subContent.addComponent(startDate);
		subContent.addComponent(hotelName);
		subContent.addComponent(minCustomer);
		subContent.addComponent(maxCustomer);

		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(confirm);
		buttonActions.addComponent(new Button("Cancel", event -> subWindow.close()));
		subContent.addComponent(buttonActions);

		Collection<TourGuide> potentialTourGuides = new ArrayList<TourGuide>();

		for (TourGuide tg : actionManager.findAvailableTourGuidesForOffering(offeringToSave)) {
			potentialTourGuides.add(tg);
		}
		tourGuide.setItems(potentialTourGuides);

		// Binding method according to docs
		Binder<Offering> binder = new Binder<>(Offering.class);

		binder.forField(tourGuide).asRequired(Utils.generateRequiredError()).bind(Offering::getTourGuide,
				Offering::setTourGuide);

		binder.forField(startDate).asRequired(Utils.generateRequiredError())
				.withConverter(new LocalDateToUtilDateConverter())
				.withValidator(ValidatorFactory.getDateNotEarlierThanValidator(Date.from(Instant.now())))
				.bind(Offering::getStartDate, Offering::setStartDate);

		binder.forField(hotelName).asRequired(Utils.generateRequiredError())
				.withValidator(ValidatorFactory.getStringLengthValidator(255))
				.bind(Offering::getHotelName, Offering::setHotelName);

		binder.forField(minCustomer).asRequired(Utils.generateRequiredError())
				.withValidator(ValidatorFactory.getIntegerLowerBoundValidator(0))
				.withConverter(new StringToIntegerConverter("Must be an integer"))
				.bind(Offering::getMinCustomers, Offering::setMinCustomers);

		binder.forField(maxCustomer).asRequired(Utils.generateRequiredError())
				.withValidator(new IntegerLowerBoundedByAnotherFieldValidator(minCustomer))
				.withValidator(ValidatorFactory.getIntegerLowerBoundValidator(0))
				.withConverter(new StringToIntegerConverter("Must be an integer"))
				.bind(Offering::getMaxCustomers, Offering::setMaxCustomers);

		// Do set bean to assign value to fields
		binder.setBean(offeringToSave);

		confirm.addClickListener(event -> {
			BinderValidationStatus<Offering> validationStatus = binder.validate();

			StringBuilder errorStringBuilder = new StringBuilder();
			if (validationStatus.isOk()) {
				binder.writeBeanIfValid(offeringToSave);
				offeringToSave.setTour(hostTour);

				log.info("About to save tour [{}]", tourName.getValue());

				try {
					actionManager.createOfferingForTour(offeringToSave);

					tourEditor.refreshData();
					this.refreshData();
					subWindow.close();
					log.info("created/edited tour [{}] successfully", tourName.getValue());
					binder.removeBean();
					return; // This return skip the error reporting procedure below
				} catch (OfferingDayOfWeekUnsupportedException e) {
					errorStringBuilder.append("The Day of Week is unsupported");
				} catch (TourGuideUnavailableException e) {
					errorStringBuilder.append("THe tour guide is unavailable");
				} catch (OfferingDateUnsupportedException e) {
					errorStringBuilder.append("The date is unsupported");
				}
			}

			for (BindingValidationStatus<?> result : validationStatus.getFieldValidationErrors()) {
				if (result.getField() instanceof AbstractField && result.getMessage().isPresent()) {
					errorStringBuilder.append(((AbstractField) result.getField()).getCaption()).append(" ")
							.append(result.getMessage().get()).append("\n");
				}
			}

			Notification.show("Could not create/edit tour!", errorStringBuilder.toString(),
					Notification.TYPE_ERROR_MESSAGE);
		});

		return subWindow;
	}

	public interface ChangeHandler {
		void onChange();
	}

	private void refreshData() {
		Iterable<Offering> offerings = offeringRepo.findAll();
		offeringsCollectionCached.clear();
		offerings.forEach(offeringsCollectionCached::add);
		ListDataProvider<Offering> provider = new ListDataProvider<>(offeringsCollectionCached);
		// tourGrid.setDataProvider(provider);
		// tourGrid.setItems(tourCollectionCached);
	}

}
