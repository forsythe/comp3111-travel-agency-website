package comp3111.editors;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import comp3111.converters.LocalDateToUtilDateConverter;
import comp3111.converters.TourGuideIDConverter;
import comp3111.exceptions.OfferingDateUnsupportedException;
import comp3111.exceptions.OfferingDayOfWeekUnsupportedException;
import comp3111.exceptions.TourGuideUnavailableException;
import comp3111.model.ActionManager;
import comp3111.model.Offering;
import comp3111.model.Tour;
import comp3111.repo.OfferingRepository;
import comp3111.validators.IntegerLowerBoundedByAnotherFieldValidator;
import comp3111.validators.Utils;
import comp3111.validators.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class TourOfferingEditor {
	private static final Logger log = LoggerFactory.getLogger(TourOfferingEditor.class);

	private OfferingRepository offeringRepo;
	private final HashSet<Offering> offeringsCollectionCached = new HashSet<>();

	@Autowired
	private TourGuideIDConverter tourGuideIDConverter;

	@Autowired
	private ActionManager actionManager;

	@SuppressWarnings("unchecked")
	@Autowired
	public TourOfferingEditor(OfferingRepository tr) {
		this.offeringRepo = tr;
	}

	Window getSubWindow(Tour hostTour, Offering offeringToSave, TourEditor tourEditor) {
		//Creating the confirm button
		Button confirm = new Button("Confirm");
		confirm.setId("confirm_offering");

		//Creating the fields
		Label tourName = new Label(hostTour.getTourName());
		TextField tourGuideName = new TextField("Tour Guide ID");
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
		subContent.addComponent(tourGuideName);
		subContent.addComponent(startDate);
		subContent.addComponent(hotelName);
		subContent.addComponent(minCustomer);
		subContent.addComponent(maxCustomer);

		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(confirm);
		buttonActions.addComponent(new Button("Cancel", event -> subWindow.close()));
		subContent.addComponent(buttonActions);

		//Binding method according to docs
		Binder<Offering> binder = new Binder<>(Offering.class);

		binder.forField(tourGuideName).withValidator(ValidatorFactory.getStringLengthValidator(255))
				.asRequired(Utils.generateRequiredError())
				.withConverter(new StringToLongConverter("Must be an integer"))
				.withConverter(tourGuideIDConverter)
				.bind(Offering::getTourGuide, Offering::setTourGuide);

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

		//Do set bean to assign value to fields
		binder.setBean(offeringToSave);

		confirm.addClickListener(event -> {
			BinderValidationStatus<Offering> validationStatus = binder.validate();

			StringBuilder errorStringBuilder = new StringBuilder();
			if (validationStatus.isOk()) {
				binder.writeBeanIfValid(offeringToSave);
				offeringToSave.setTour(hostTour);

				log.info("About to save tour [{}]", tourName.getValue());

				try{
					actionManager.createOfferingForTour(offeringToSave);

					tourEditor.refreshData();
					this.refreshData();
					subWindow.close();
					log.info("created/edited tour [{}] successfully", tourName.getValue());
					binder.removeBean();
					return; //This return skip the error reporting procedure below
				}catch (OfferingDayOfWeekUnsupportedException e){
					errorStringBuilder.append("The Day of Week is unsupported");
				}catch (TourGuideUnavailableException e){
					errorStringBuilder.append("THe tour guide is unavailable");
				}catch (OfferingDateUnsupportedException e){
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
		//tourGrid.setDataProvider(provider);
		// tourGrid.setItems(tourCollectionCached);
	}

}
