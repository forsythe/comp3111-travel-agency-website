package comp3111.input.editors;

import java.time.Instant;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import comp3111.Utils;
import comp3111.data.DB;
import comp3111.data.DBManager;
import comp3111.data.model.Offering;
import comp3111.data.model.Tour;
import comp3111.data.model.TourGuide;
import comp3111.data.repo.OfferingRepository;
import comp3111.data.repo.TourGuideRepository;
import comp3111.input.converters.ConverterFactory;
import comp3111.input.converters.LocalDateToUtilDateConverter;
import comp3111.input.exceptions.OfferingDateUnsupportedException;
import comp3111.input.exceptions.TourGuideUnavailableException;
import comp3111.input.validators.ValidatorFactory;
import comp3111.view.NotificationFactory;
import comp3111.view.TourManagementView;

@SpringComponent
@UIScope
public class OfferingEditor extends VerticalLayout {
	private static final Logger log = LoggerFactory.getLogger(OfferingEditor.class);

	private OfferingRepository offeringRepo;

	@Autowired
	private TourGuideRepository tourGuideRepo;

	private TourEditor tourEditor;

	@Autowired
	private DBManager dbManager;

	private Tour selectedTour;
	private final Grid<Offering> offeringGrid = new Grid<Offering>(Offering.class);

	private Offering selectedOffering;

	/* Action buttons */
	private HorizontalLayout rowOfButtons = new HorizontalLayout();
	private Button createNewOfferingButton = new Button("Create New Offering");
	private Button editOfferingButton = new Button("Edit Offering");
	private Button returnButton = new Button("Return");

	@Autowired
	public OfferingEditor(OfferingRepository or) {

		this.offeringRepo = or;

		rowOfButtons.addComponent(createNewOfferingButton);
		rowOfButtons.addComponent(editOfferingButton);
		rowOfButtons.addComponent(returnButton);

		createNewOfferingButton.setId("btn_create_new_offering");
		editOfferingButton.setId("btn_edit_offering");
		returnButton.setId("btn_return_offering");

		editOfferingButton.setEnabled(false);

		this.addComponent(rowOfButtons);

		this.refreshData();

		offeringGrid.setWidth("100%");
		offeringGrid.setSelectionMode(SelectionMode.SINGLE);

		offeringGrid.addSelectionListener(event -> {
			if (event.getFirstSelectedItem().isPresent()) {
				selectedOffering = event.getFirstSelectedItem().get();
				createNewOfferingButton.setEnabled(false);
				editOfferingButton.setEnabled(true);
			} else {
				selectedOffering = null;
				createNewOfferingButton.setEnabled(true);
				editOfferingButton.setEnabled(false);
			}
		});

		offeringGrid.removeColumn(DB.OFFERING_TOUR); // we'll combine days of week and dates
		offeringGrid.removeColumn(DB.OFFERING_TOUR_GUIDE);
		offeringGrid.removeColumn(DB.OFFERING_DATE);
		offeringGrid.removeColumn(DB.OFFERING_LAST_EDITABLE_DATE);

		offeringGrid.setColumnOrder(DB.OFFERING_ID, DB.OFFERING_STATUS, DB.OFFERING_START_DATE,
				DB.OFFERING_TOUR_GUIDE_NAME, DB.OFFERING_TOUR_GUIDE_LINE_ID, DB.OFFERING_TOUR_NAME,
				DB.OFFERING_MIN_CAPACITY, DB.OFFERING_MAX_CAPACITY);

		offeringGrid.getColumn(DB.OFFERING_START_DATE).setCaption("Start Date");

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
		DateField startDate = Utils.getDateFieldWithOurLocale("Start Date");
		Label availablityHint = new Label("Can be offered on " + hostTour.getOfferingAvailability());

		TextField hotelName = new TextField("Hotel Name");
		TextField minCustomer = new TextField("Min number of customer");
		TextField maxCustomer = new TextField("Max number of customer");
		Label statusHint = new Label();
		statusHint.setWidth("100%");

		Window subWindow = new Window("Create new offering");

		tourGuide.setPopupWidth(null);

		tourGuide.setItems(Utils.iterableToCollection(tourGuideRepo.findAll()));

		FormLayout form = new FormLayout();
		VerticalLayout formContainer = new VerticalLayout();
		formContainer.addComponent(form);

		subWindow.setWidth("800px");
		subWindow.setContent(formContainer);

		subWindow.center();
		subWindow.setClosable(false);
		subWindow.setModal(true);
		subWindow.setResizable(false);
		subWindow.setDraggable(false);

		form.addComponent(tourName);
		form.addComponent(availablityHint);

		form.addComponent(startDate);
		form.addComponent(tourGuide);
		form.addComponent(hotelName);
		form.addComponent(minCustomer);
		form.addComponent(maxCustomer);
		form.addComponent(statusHint);

		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(confirm);
		buttonActions.addComponent(new Button("Cancel", event -> subWindow.close()));
		form.addComponent(buttonActions);

		// Binding method according to docs
		Binder<Offering> binder = new Binder<>(Offering.class);

		binder.forField(tourGuide).asRequired(Utils.generateRequiredError()).withValidator(
				ValidatorFactory.getTourGuideAvailableForDatesValidation(startDate, hostTour.getDays(), dbManager))
				.bind(Offering::getTourGuide, Offering::setTourGuide);

		binder.forField(startDate).asRequired(Utils.generateRequiredError())
				.withConverter(new LocalDateToUtilDateConverter())
				.withValidator(ValidatorFactory.getDateNotEarlierThanValidator(Date.from(Instant.now())))
				.withValidator(ValidatorFactory.getDateAvailableInTourValidator(hostTour))
				.bind(Offering::getStartDate, Offering::setStartDate);

		binder.forField(hotelName).asRequired(Utils.generateRequiredError())
				.withValidator(ValidatorFactory.getStringLengthValidator(255))
				.bind(Offering::getHotelName, Offering::setHotelName);

		binder.forField(minCustomer).asRequired(Utils.generateRequiredError())
				.withConverter(ConverterFactory.getStringToIntegerConverter())
				.withValidator(ValidatorFactory.getIntegerRangeValidator(0))
				.bind(Offering::getMinCustomers, Offering::setMinCustomers);

		binder.forField(maxCustomer).asRequired(Utils.generateRequiredError())
				.withConverter(ConverterFactory.getStringToIntegerConverter())
				.withValidator(ValidatorFactory.getIntegerLowerBoundedByAnotherFieldValidator(minCustomer))
				.withValidator(ValidatorFactory.getIntegerRangeValidator(0))
				.bind(Offering::getMaxCustomers, Offering::setMaxCustomers);

		// Do set bean to assign value to fields
		binder.setBean(offeringToSave);

		startDate.addValueChangeListener(event -> {
			statusHint.setValue(
					"All participating customers will automatically be notified whether this offering is confirmed or cancelled on "
							+ Utils.simpleDateFormat(Utils.localDateToDate(startDate.getValue())) + ".");
		});

		confirm.addClickListener(event -> {
			BinderValidationStatus<Offering> validationStatus = binder.validate();

			String errors = ValidatorFactory.getValidatorErrorsString(validationStatus);
			if (validationStatus.isOk()) {
				binder.writeBeanIfValid(offeringToSave);
				offeringToSave.setTour(hostTour);
				offeringToSave.setStatus(Offering.STATUS_PENDING);

				log.info("About to save tour [{}]", tourName.getValue());

				try {
					dbManager.createOfferingForTour(offeringToSave);

					tourEditor.refreshData();
					this.refreshData();
					subWindow.close();
					log.info("created/edited offering [{}] successfully", tourName.getValue());
					NotificationFactory.getTopBarSuccessNotification().show(Page.getCurrent());

					binder.removeBean();
					return; // This return skip the error reporting procedure below
				} catch (OfferingDateUnsupportedException e) {
					errors += "This tour may only be offered on " + hostTour.getOfferingAvailability() + "\n";

				} catch (TourGuideUnavailableException e) {
					errors += "The tour guide is occupied\n";
				}
			}

			NotificationFactory.getTopBarWarningNotification(errors, 5).show(Page.getCurrent());

		});

		return subWindow;
	}

	public interface ChangeHandler {
		void onChange();
	}

	public void refreshData() {

		ListDataProvider<Offering> provider = new ListDataProvider<>(
				Utils.iterableToCollection(offeringRepo.findByTour(this.selectedTour)));
		offeringGrid.setDataProvider(provider);
	}

	// Helpers for accessing stuff from tourEditor
	public void setSelectedTour(Tour selectedTour) {
		this.selectedTour = selectedTour;
	}

	public Tour getSelectedTour() {
		return this.selectedTour;
	}

	public void setTourEditor(TourEditor te) {
		this.tourEditor = te;
	}
}
