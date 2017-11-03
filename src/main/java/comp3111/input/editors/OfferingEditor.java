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
import com.vaadin.server.Sizeable.Unit;
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
	private Button cancelOfferingButton = new Button("Manually Cancel Offering");
	private Button returnButton = new Button("Return");

	private final HashMap<String, ProviderAndPredicate<?, ?>> gridFilters = new HashMap<String, ProviderAndPredicate<?, ?>>();

	@Autowired
	public OfferingEditor(OfferingRepository or) {

		this.offeringRepo = or;

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

		offeringGrid.addColumn(offering -> {
			return dbManager.countNumberOfPaidPeopleInOffering(offering);
		}).setId(GridCol.OFFERING_NUM_CONFIRMED_CUSTOMERS).setCaption("Confirmed Participants");

		offeringGrid.setColumnOrder(GridCol.OFFERING_ID, GridCol.OFFERING_STATUS, GridCol.OFFERING_START_DATE,
				GridCol.OFFERING_TOUR_GUIDE_NAME, GridCol.OFFERING_TOUR_GUIDE_LINE_ID, GridCol.OFFERING_TOUR_NAME,
				GridCol.OFFERING_NUM_CONFIRMED_CUSTOMERS, GridCol.OFFERING_MIN_CAPACITY, GridCol.OFFERING_MAX_CAPACITY);

		HeaderRow filterRow = offeringGrid.appendHeaderRow();

		for (Column<Offering, ?> col : offeringGrid.getColumns()) {
			col.setMinimumWidth(120);
			col.setHidable(true);
			col.setHidingToggleCaption(col.getCaption());
			col.setExpandRatio(1);
			HeaderCell cell = filterRow.getCell(col.getId());

			// Have an input field to use for filter
			TextField filterField = new TextField();
			filterField.setWidth(130, Unit.PIXELS);
			filterField.setHeight(30, Unit.PIXELS);

			filterField.addValueChangeListener(change -> {
				String searchVal = change.getValue();
				String colId = col.getId();

				log.info("Value change in col [{}], val=[{}]", colId, searchVal);
				ListDataProvider<Offering> dataProvider = (ListDataProvider<Offering>) offeringGrid.getDataProvider();

				if (!filterField.isEmpty()) {
					try {
						// note: if we keep typing into same textfield, we will overwrite the old filter
						// for this column, which is desirable (rather than having filters for "h",
						// "he", "hel", etc
						gridFilters.put(colId, FilterFactory.getFilterForOffering(colId, searchVal));
						log.info("updated filter on attribute [{}]", colId);

					} catch (Exception e) {
						log.info("ignoring val=[{}], col=[{}] is invalid", searchVal, colId);
					}
				} else {
					// the filter field was empty, so try
					// removing the filter associated with this col
					gridFilters.remove(colId);
					log.info("removed filter on attribute [{}]", colId);

				}
				dataProvider.clearFilters();
				for (String colFilter : gridFilters.keySet()) {
					ProviderAndPredicate paf = gridFilters.get(colFilter);
					dataProvider.addFilter(paf.provider, paf.predicate);
				}
				dataProvider.refreshAll();
			});
			cell.setComponent(filterField);
		}

		this.addComponent(offeringGrid);

		createNewOfferingButton.addClickListener(event -> {
			UI.getCurrent().addWindow(getSubWindow(selectedTour, new Offering(), tourEditor));
		});

		editOfferingButton.addClickListener(event -> {
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

	Window getSubWindow(Tour hostTour, final Offering offeringToSave, TourEditor tourEditor) {
		boolean isCreatingNewOffering = offeringToSave.getId() == null;

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

		Window subWindow;
		if (isCreatingNewOffering) {
			subWindow = new Window("Create new offering");
		} else {
			subWindow = new Window("Edit an offering");
		}

		tourGuide.setPopupWidth(null);

		tourGuide.setItems(Utils.iterableToCollection(tourGuideRepo.findAll()).stream()
				.sorted((tg1, tg2) -> tg1.getId().compareTo(tg2.getId())));

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

		binder.forField(tourGuide).asRequired(Utils.generateRequiredError())
				.withValidator(ValidatorFactory.getTourGuideAvailableForDatesValidationIgnoreOneOffering(startDate,
						hostTour.getDays(), dbManager, isCreatingNewOffering ? null : offeringToSave))
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
			if (!startDate.isEmpty()) {
				Date threeDaysBeforeStart = Utils.addDate(Utils.localDateToDate(startDate.getValue()), -3);
				statusHint.setValue(
						"All participating customers will automatically be notified whether this offering is confirmed or cancelled on "
								+ Utils.simpleDateFormat(threeDaysBeforeStart));
			}
		});

		confirm.addClickListener(event -> {
			BinderValidationStatus<Offering> validationStatus = binder.validate();

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
