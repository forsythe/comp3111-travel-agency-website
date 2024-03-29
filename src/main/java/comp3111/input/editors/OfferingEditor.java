package comp3111.input.editors;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

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
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import comp3111.LineMessenger;
import comp3111.Utils;
import comp3111.data.DBManager;
import comp3111.data.GridCol;
import comp3111.data.model.Booking;
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

/**
 * Represents the offering editor in the OfferingManagementView
 * 
 * @author Forsythe
 *
 */
@SpringComponent
@UIScope
public class OfferingEditor extends VerticalLayout {
	private static final Logger log = LoggerFactory.getLogger(OfferingEditor.class);

	private OfferingRepository offeringRepo;

	private TourGuideRepository tourGuideRepo;

	private TourEditor tourEditor;

	private DBManager dbManager;

	private Window subWindow;

	private Tour selectedTour;
	private final Grid<Offering> offeringGrid = new Grid<Offering>(Offering.class);

	private Offering selectedOffering;

	/* Action buttons */
	private HorizontalLayout rowOfButtons = new HorizontalLayout();
	private Button createNewOfferingButton = new Button("Create New Offering");
	private Button editOfferingButton = new Button("Edit Offering");
	private Button cancelOfferingButton = new Button("Manually Cancel Offering");
	private Button returnButton = new Button("Return");

	private Button subwindowConfirmButton;

	private TextField tourName;
	private ComboBox<TourGuide> tourGuide;
	private DateField startDate;
	private TextField hotelName;
	private TextField minCustomer;
	private TextField maxCustomer;

	BinderValidationStatus<Offering> validationStatus;

	private final HashMap<String, ProviderAndPredicate<?, ?>> gridFilters = new HashMap<String, ProviderAndPredicate<?, ?>>();

	/**
	 * Constructs the editor for creating/editing Offerings
	 * 
	 * @param or
	 *            The OfferingRepository
	 * @param tgr
	 *            The TourGuideRepository
	 * @param dbm
	 *            The DBManager
	 */
	@Autowired
	public OfferingEditor(OfferingRepository or, TourGuideRepository tgr, DBManager dbm) {

		this.offeringRepo = or;
		this.tourGuideRepo = tgr;
		this.dbManager = dbm;

		rowOfButtons.addComponent(createNewOfferingButton);
		rowOfButtons.addComponent(editOfferingButton);
		rowOfButtons.addComponent(cancelOfferingButton);
		rowOfButtons.addComponent(returnButton);

		createNewOfferingButton.setId("btn_create_new_offering");
		editOfferingButton.setId("btn_edit_offering");
		cancelOfferingButton.setId("btn_cancel_offering");
		returnButton.setId("btn_return_offering");

		editOfferingButton.setEnabled(false);
		cancelOfferingButton.setEnabled(false);

		this.addComponent(rowOfButtons);

		this.refreshData();

		offeringGrid.setWidth("100%");
		offeringGrid.setSelectionMode(SelectionMode.SINGLE);

		offeringGrid.addSelectionListener(event -> {
			if (event.getFirstSelectedItem().isPresent()) {
				selectedOffering = event.getFirstSelectedItem().get();
				createNewOfferingButton.setEnabled(false);
				editOfferingButton.setEnabled(true);
				if (selectedOffering.getStatus().equals(Offering.STATUS_PENDING))
					cancelOfferingButton.setEnabled(true);
			} else {
				selectedOffering = null;
				createNewOfferingButton.setEnabled(true);
				editOfferingButton.setEnabled(false);
				cancelOfferingButton.setEnabled(false);
			}
		});

		offeringGrid.removeColumn(GridCol.OFFERING_TOUR); // we'll combine days of week and dates
		offeringGrid.removeColumn(GridCol.OFFERING_TOUR_GUIDE);
		offeringGrid.removeColumn(GridCol.OFFERING_DATE);
		offeringGrid.removeColumn(GridCol.OFFERING_LAST_EDITABLE_DATE);

		offeringGrid.getColumn(GridCol.OFFERING_START_DATE).setCaption("Start Date");

		offeringGrid.setColumnOrder(GridCol.OFFERING_ID, GridCol.OFFERING_STATUS, GridCol.OFFERING_START_DATE,
				GridCol.OFFERING_TOUR_GUIDE_NAME, GridCol.OFFERING_TOUR_GUIDE_LINE_ID, GridCol.OFFERING_TOUR_NAME,
				GridCol.OFFERING_MIN_CAPACITY, GridCol.OFFERING_MAX_CAPACITY);

		FilterFactory.addFilterInputBoxesToGridHeaderRow(Offering.class, offeringGrid, gridFilters);

		this.addComponent(offeringGrid);

		createNewOfferingButton.addClickListener(event -> {
			UI.getCurrent().addWindow(getSubWindow(selectedTour, new Offering(), tourEditor));
		});

		editOfferingButton.addClickListener(event -> {
			if (canEditOffering(selectedOffering))
				UI.getCurrent().addWindow(getSubWindow(selectedTour, selectedOffering, tourEditor));
		});

		returnButton.addClickListener(event -> {
			getUI().getNavigator().navigateTo(TourManagementView.VIEW_NAME);
		});

		cancelOfferingButton.addClickListener(event -> {
			final Window confirmWindow = new Window("Are you sure?");
			confirmWindow.center();
			confirmWindow.setClosable(false);
			confirmWindow.setModal(true);
			confirmWindow.setResizable(false);
			confirmWindow.setDraggable(false);
			confirmWindow.setWidth("500px");

			VerticalLayout vLayout = new VerticalLayout();
			confirmWindow.setContent(vLayout);
			Label msg = new Label("All associated customer bookings will be cancelled, "
					+ "and the customers will be notified via LINE.");
			msg.setWidth("100%");
			vLayout.addComponent(msg);

			HorizontalLayout buttonRow = new HorizontalLayout();
			Button confirm = new Button("Yes");
			Button cancel = new Button("Cancel");

			buttonRow.addComponent(confirm);
			buttonRow.addComponent(cancel);
			vLayout.addComponent(buttonRow);

			confirm.addClickListener(e -> {
				LineMessenger.resetCounter();
				dbManager.notifyOfferingStatus(selectedOffering, false);
				this.refreshData();
				NotificationFactory
						.getTopBarSuccessNotification("Notified " + LineMessenger.getCounter() + " customer(s)")
						.show(Page.getCurrent());
				confirmWindow.close();
			});

			cancel.addClickListener(e -> {
				confirmWindow.close();
			});

			UI.getCurrent().addWindow(confirmWindow);
		});

	}

	public Window getSubWindow(Tour hostTour, final Offering offeringToSave, TourEditor tourEditor) {
		boolean isCreatingNewOffering = offeringToSave.getId() == null;

		// Creating the confirm button
		subwindowConfirmButton = new Button("Confirm");
		getSubwindowConfirmButton().setId("btn_confirm_offering");

		// Creating the fields
		tourName = new TextField("Tour");
		tourName.setId("tf_offering_name");
		tourName.setValue(hostTour.getTourName());
		tourName.setEnabled(false);

		tourGuide = new ComboBox<TourGuide>("Tour Guide");
		tourGuide.setId("cb_offering_tour_guide");
		startDate = Utils.getDateFieldWithOurLocale("Start Date");
		startDate.setId("tf_offering_start_date");
		Label availabilityHint = new Label("Can be offered on " + hostTour.getOfferingAvailability());

		hotelName = new TextField("Hotel Name");
		hotelName.setId("tf_offering_hotel_name");
		minCustomer = new TextField("Min number of customer");
		minCustomer.setId("tf_offering_min_customer");
		maxCustomer = new TextField("Max number of customer");
		maxCustomer.setId("tf_offering_max_customer");
		Label statusHint = new Label();
		statusHint.setWidth("100%");

		if (isCreatingNewOffering) {
			subWindow = new Window("Create new offering");
		} else {
			subWindow = new Window("Edit an offering");
		}

		tourGuide.setPopupWidth(null);

		Iterable<TourGuide> tourGuidesIterable = tourGuideRepo.findAll();
		if (tourGuidesIterable != null) {
			tourGuide.setItems(Utils.iterableToCollection(tourGuidesIterable).stream()
					.sorted((tg1, tg2) -> tg1.getId().compareTo(tg2.getId())));
		}

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
		form.addComponent(availabilityHint);

		form.addComponent(startDate);
		form.addComponent(tourGuide);
		form.addComponent(hotelName);
		form.addComponent(minCustomer);
		form.addComponent(maxCustomer);
		form.addComponent(statusHint);

		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(getSubwindowConfirmButton());
		buttonActions.addComponent(new Button("Cancel", event -> subWindow.close()));
		form.addComponent(buttonActions);

		// Binding method according to docs
		Binder<Offering> binder = new Binder<>(Offering.class);

		binder.forField(tourGuide).asRequired(Utils.generateRequiredError())
				.withValidator(ValidatorFactory.getTourGuideAvailableForDatesValidaterIgnoreOneOffering(startDate,
						hostTour.getDays(), dbManager, isCreatingNewOffering ? null : offeringToSave))
				.bind(Offering::getTourGuide, Offering::setTourGuide);

		binder.forField(startDate).asRequired(Utils.generateRequiredError())
				.withConverter(new LocalDateToUtilDateConverter())
				.withValidator(
						ValidatorFactory.getDateNotEarlierThanValidator(Utils.addDate(Date.from(Instant.now()), 3)))
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
			if (!startDate.isEmpty()) {
				Date threeDaysBeforeStart = Utils.addDate(Utils.localDateToDate(startDate.getValue()), -3);
				statusHint.setValue(
						"All participating customers will automatically be notified whether this offering is confirmed or cancelled on "
								+ Utils.simpleDateFormat(threeDaysBeforeStart));
			}
		});

		subwindowConfirmButton.addClickListener(event -> {
			validationStatus = binder.validate();

			String errors = ValidatorFactory.getValidatorErrorsString(validationStatus);
			if (validationStatus.isOk()) {

				binder.writeBeanIfValid(offeringToSave);

				if (isCreatingNewOffering) {
					offeringToSave.setTour(hostTour);
					offeringToSave.setStatus(Offering.STATUS_PENDING);
				}

				try {
					if (isCreatingNewOffering) {
						log.info("About to save offering [{}]", tourName.getValue());
						dbManager.createOfferingForTour(offeringToSave);
						log.info("created offering [{}] successfully", tourName.getValue());

					} else {
						log.info("About to edit offering [{}]", tourName.getValue());
						dbManager.editOfferingTorTour(offeringToSave);
						log.info("edited offering [{}] successfully", tourName.getValue());
					}

					tourEditor.refreshData();
					this.refreshData();
					subWindow.close();
					if (Page.getCurrent() != null)
						NotificationFactory.getTopBarSuccessNotification().show(Page.getCurrent());

					binder.removeBean();
					return; // This return skip the error reporting procedure below
				} catch (OfferingDateUnsupportedException e) {
					errors += "This tour may only be offered on " + hostTour.getOfferingAvailability() + "\n";

				} catch (TourGuideUnavailableException e) {
					errors += "The tour guide is occupied\n";
				}
			}
			if (Page.getCurrent() != null)
				NotificationFactory.getTopBarWarningNotification(errors, 5).show(Page.getCurrent());

		});

		return subWindow;
	}

	/**
	 * Check whether an offering is editable or not based on start date and current
	 * date. If the offering is cancelled but has not reached the time of
	 * confirmation, it also returns false, but displays a different error message
	 * 
	 * @param offering
	 *            The offering to check
	 * @return Whether the offering is editable
	 */
	public boolean canEditOffering(Offering offering) {
		if (offering == null)
			return false;

		Date today = Date.from(Instant.now());
		Date threeDayBeforeStart = offering.getLastEditableDate();

		if (today.after(threeDayBeforeStart)) {
			if (Page.getCurrent() != null) // can be null if using mockito
				NotificationFactory.getTopBarWarningNotification(
						"It's too late to edit this offering. Its status was finalized on "
								+ Utils.simpleDateFormat(threeDayBeforeStart),
						5).show(Page.getCurrent());

			return false;
		} else if (offering.getStatus().equals(Offering.STATUS_CANCELLED)) {
			if (Page.getCurrent() != null) // can be null if using mockito
				NotificationFactory
						.getTopBarWarningNotification("It's too late to edit this offering. It has been cancelled.", 5)
						.show(Page.getCurrent());
			return false;
		}

		return true;
	}

	/**
	 * Refreshes the data in the vaadin grid
	 */
	public void refreshData() {

		ListDataProvider<Offering> provider = new ListDataProvider<>(
				Utils.iterableToCollection(offeringRepo.findByTour(this.selectedTour)));
		offeringGrid.setDataProvider(provider);
	}

	public Button getSubwindowConfirmButton() {
		return subwindowConfirmButton;
	}

	public TextField getTourName() {
		return tourName;
	}

	public ComboBox<TourGuide> getTourGuide() {
		return tourGuide;
	}

	public DateField getStartDate() {
		return startDate;
	}

	public TextField getHotelName() {
		return hotelName;
	}

	public TextField getMinCustomer() {
		return minCustomer;
	}

	public TextField getMaxCustomer() {
		return maxCustomer;
	}

	public BinderValidationStatus<Offering> getValidationStatus() {
		return validationStatus;
	}

	// Helpers for accessing stuff from tourEditor
	/**
	 * @param selectedTour
	 *            Set the selected tour
	 */
	public void setSelectedTour(Tour selectedTour) {
		this.selectedTour = selectedTour;
	}

	/**
	 * @return get the selected tour object
	 */
	public Tour getSelectedTour() {
		return this.selectedTour;
	}

	/**
	 * @param te
	 *            Set the parent TourEditor object
	 */
	public void setTourEditor(TourEditor te) {
		this.tourEditor = te;
	}
}
