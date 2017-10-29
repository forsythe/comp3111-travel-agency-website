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
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import comp3111.Utils;
import comp3111.data.DB;
import comp3111.data.DBManager;
import comp3111.data.model.Booking;
import comp3111.data.model.Customer;
import comp3111.data.model.Offering;
import comp3111.data.model.Tour;
import comp3111.data.model.TourGuide;
import comp3111.data.repo.OfferingRepository;
import comp3111.data.repo.TourGuideRepository;
import comp3111.input.converters.LocalDateToUtilDateConverter;
import comp3111.input.converters.TourGuideIDConverter;
import comp3111.input.exceptions.OfferingDateUnsupportedException;
import comp3111.input.exceptions.OfferingDayOfWeekUnsupportedException;
import comp3111.input.exceptions.TourGuideUnavailableException;
import comp3111.input.validators.IntegerLowerBoundedByAnotherFieldValidator;
import comp3111.input.validators.ValidatorFactory;
import comp3111.view.OfferingManagementView;
import comp3111.view.TourManagementView;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class OfferingEditor extends VerticalLayout{
	private static final Logger log = LoggerFactory.getLogger(OfferingEditor.class);
	
	private OfferingRepository offeringRepo;
	private final HashSet<Offering> offeringsCollectionCached = new HashSet<>();

	@Autowired
	private TourGuideRepository tourGuideRepo;
	
	TourEditor tourEditor;

	@Autowired
	private DBManager actionManager;
	
	private Tour selectedTour;
	private final Grid<Offering> offeringGrid = new Grid<Offering>(Offering.class);
	
	private Offering selectedOffering;
	
	/* Action buttons */
	HorizontalLayout rowOfButtons = new HorizontalLayout();
	private Button createNewOfferingButton = new Button("Create New Offering");
	private Button editOfferingButton = new Button("Edit Offering");
	private Button returnButton = new Button("Return");

	@SuppressWarnings("unchecked")
	@Autowired
	public OfferingEditor(OfferingRepository or) {
		this.offeringRepo = or;
		
		rowOfButtons.addComponent(createNewOfferingButton);
		rowOfButtons.addComponent(editOfferingButton);
		rowOfButtons.addComponent(returnButton);
		createNewOfferingButton.setId("btn_create_new_offering");
		editOfferingButton.setId("btn_edit_offering");
		returnButton.setId("btn_return_offering");
		
		this.addComponent(rowOfButtons);
		
		this.refreshData();
		
		offeringGrid.setWidth("100%");
		offeringGrid.setSelectionMode(SelectionMode.SINGLE);
		
		offeringGrid.addSelectionListener(event -> {
			if (event.getFirstSelectedItem().isPresent()) {
				selectedOffering = event.getFirstSelectedItem().get();
			} else {
				selectedOffering = null;
			}
		});
		
		offeringGrid.removeColumn(DB.OFFERING_TOUR); // we'll combine days of week and dates
		offeringGrid.removeColumn(DB.OFFERING_TOUR_GUIDE);
		offeringGrid.removeColumn(DB.OFFERING_DATE);
		
		offeringGrid.setColumnOrder(DB.OFFERING_ID, DB.OFFERING_START_DATE, DB.OFFERING_TOUR_GUIDE_NAME,
				DB.OFFERING_TOUR_GUIDE_LINE_ID, DB.OFFERING_TOUR_NAME, DB.OFFERING_MIN_CAPACITY, 
				DB.OFFERING_MAX_CAPACITY);
		
		for (Column<Offering, ?> col : offeringGrid.getColumns()) {
			col.setMinimumWidth(120);
			col.setHidable(true);
			col.setHidingToggleCaption(col.getCaption());
			col.setExpandRatio(1);
		}
		
		this.addComponent(offeringGrid);
		
		createNewOfferingButton.addClickListener(event -> {
			getUI().getCurrent().addWindow(getSubWindow(selectedTour, new Offering(), tourEditor));
		});
		
		returnButton.addClickListener(event -> {
			getUI().getNavigator().navigateTo(TourManagementView.VIEW_NAME);
		});
			
		
	}

	Window getSubWindow(Tour hostTour, Offering offeringToSave, TourEditor tourEditor) {
		// Creating the confirm button
		Button confirm = new Button("Confirm");
		confirm.setId("btn_confirm_offering");

		// Creating the fields
		TextField tourName = new TextField("Tour");
		tourName.setValue(hostTour.getTourName());
		tourName.setEnabled(false);

		ComboBox<TourGuide> tourGuide = new ComboBox<TourGuide>("Tour Guide");
		DateField startDate = new DateField("Start Date");
		Label availablityHint = new Label("Can be offered on " + hostTour.getOfferingAvailability());

		TextField hotelName = new TextField("Hotel Name");
		TextField minCustomer = new TextField("Min number of customer");
		TextField maxCustomer = new TextField("Max number of customer");

		Window subWindow = new Window("Create new offering");

		tourGuide.setPopupWidth(null);
		Iterable<TourGuide> tourGuideList = tourGuideRepo.findAll();
		Collection<TourGuide> tourGuideCollection = new HashSet<TourGuide>();
		tourGuideList.forEach(tourGuideCollection::add);
		tourGuide.setItems(tourGuideCollection);

		FormLayout subContent = new FormLayout();

		subWindow.setWidth("800px");

		subWindow.setContent(subContent);
		subWindow.center();
		subWindow.setClosable(false);
		subWindow.setModal(true);
		subWindow.setResizable(false);
		subWindow.setDraggable(false);

		subContent.addComponent(tourName);
		subContent.addComponent(availablityHint);

		subContent.addComponent(startDate);
		subContent.addComponent(tourGuide);
		subContent.addComponent(hotelName);
		subContent.addComponent(minCustomer);
		subContent.addComponent(maxCustomer);

		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(confirm);
		buttonActions.addComponent(new Button("Cancel", event -> subWindow.close()));
		subContent.addComponent(buttonActions);

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
					log.info("created/edited offering [{}] successfully", tourName.getValue());
					binder.removeBean();
					return; // This return skip the error reporting procedure below
				} catch (OfferingDayOfWeekUnsupportedException | OfferingDateUnsupportedException e) {
					errorStringBuilder.append("This tour may only be offered on " + hostTour.getOfferingAvailability());

				} catch (TourGuideUnavailableException e) {
					errorStringBuilder.append("The tour guide is occupied.");
				}
			}

			for (BindingValidationStatus<?> result : validationStatus.getFieldValidationErrors()) {
				if (result.getField() instanceof AbstractField && result.getMessage().isPresent()) {
					errorStringBuilder.append(((AbstractField) result.getField()).getCaption()).append(" ")
							.append(result.getMessage().get()).append("\n");
				}
			}

			Notification.show("Could not create/edit offering!", errorStringBuilder.toString(),
					Notification.TYPE_ERROR_MESSAGE);
		});

		return subWindow;
	}

	public interface ChangeHandler {
		void onChange();
	}
	
	public void refreshData() {
		offeringsCollectionCached.clear();
		for (Offering o : offeringRepo.findAll()) {
			if (o.getTour().equals(this.selectedTour))
				offeringsCollectionCached.add(o);
			
		}
//		offerings.forEach(offeringsCollectionCached::add);
		ListDataProvider<Offering> provider = new ListDataProvider<>(offeringsCollectionCached);
		offeringGrid.setDataProvider(provider);
	}

	//Helpers for accessing stuff from tourEditor
	public void setSelectedTour (Tour selectedTour) {
		this.selectedTour = selectedTour;
	}
	
	public void setTourEditor (TourEditor te) {
		this.tourEditor = te;
	}
}
