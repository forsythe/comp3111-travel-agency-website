package comp3111.input.editors;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

import comp3111.Utils;
import comp3111.data.DB;
import comp3111.data.DBManager;
import comp3111.data.model.Offering;
import comp3111.data.model.Tour;
import comp3111.data.repo.TourRepository;
import comp3111.view.HomeView;
import comp3111.view.OfferingManagementView;
import comp3111.view.TourManagementView;
import comp3111.input.converters.StringCollectionToIntegerCollectionConverter;
import comp3111.input.converters.StringToDateCollectionConverter;
import comp3111.input.validators.ValidatorFactory;

/**
 * A simple example to introduce building forms. As your real application is
 * probably much more complicated than this example, you could re-use this form
 * in multiple places. This example component is only used in VaadinUI.
 * <p>
 * In a real world application you'll most likely using a common super class for
 * all your forms - less code, better UX. See e.g. AbstractForm in Viritin
 * (https://vaadin.com/addon/viritin).
 */
@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class TourEditor extends VerticalLayout {
	private static final Logger log = LoggerFactory.getLogger(TourEditor.class);

	/* the popup "Create tour" window */
	// private Window createTourSubwindow;
	// private Window editTourSubwindow;
	private Window subwindow;

	@Autowired
	private DBManager dbManager;

	@Autowired
	private OfferingEditor offeringEditor;

	/* Fields to edit properties in Tour entity */
	private TextField tourName;
	private TextField days;
	private RadioButtonGroup<String> tourType;
	private CheckBoxGroup<String> allowedDaysOfWeek;
	private TextField allowedDates;
	private TextField childDiscount;
	private TextField toddlerDiscount;
	private TextField weekdayPrice;
	private TextField weekendPrice;
	private TextArea descrip;

	/* Action buttons */
	HorizontalLayout rowOfButtons = new HorizontalLayout();
	private Button createTourButton = new Button("Create new tour");
	private Button editTourButton = new Button("Edit tour");
	private Button manageOfferingButton = new Button("Manage offerings for selected tour");
	/* subwindow action buttons */
	private Button subwindowConfirm;

	// this is FINAL so we can access it inside our filtering callback function
	private final Grid<Tour> tourGrid = new Grid<Tour>(Tour.class);

	/* The currently edited tour */
	Tour selectedTour;

	private TourRepository tourRepo;

	// final just means we cant rebind the var name. we can still add/remove
	// IMPORTANT: this is FINAL so we can access it inside the filtering callback
	// function
	private final HashSet<Tour> tourCollectionCached = new HashSet<Tour>();
	// the set of filters to apply on our table
	private final HashMap<String, ProviderAndPredicate<?, ?>> gridFilters = new HashMap<String, ProviderAndPredicate<?, ?>>();

	@SuppressWarnings("unchecked")
	@Autowired
	public TourEditor(TourRepository tr) {
		this.tourRepo = tr;
		// adding components
		rowOfButtons.addComponent(createTourButton);
		rowOfButtons.addComponent(editTourButton);
		rowOfButtons.addComponent(manageOfferingButton);
		createTourButton.setId("button_create_tour");
		editTourButton.setId("button_edit_tour");
		manageOfferingButton.setId("button_manage_tour_offerings");

		// edit and manage shouldn't be enabled with no tour selected
		editTourButton.setEnabled(false);
		manageOfferingButton.setEnabled(false);

		this.addComponent(rowOfButtons);

		// get the repetaingTours from DB
		this.refreshData();

		tourGrid.setWidth("100%");
		tourGrid.setSelectionMode(SelectionMode.SINGLE);

		tourGrid.addSelectionListener(new SelectionListener<Tour>() {
			@Override
			public void selectionChange(SelectionEvent event) {
				Collection<Tour> selectedItems = tourGrid.getSelectionModel().getSelectedItems();
				selectedTour = null;
				for (Tour rt : selectedItems) { // easy way to get first element of set
					selectedTour = rt;
					break;
				}
				if (selectedTour != null) {
					editTourButton.setEnabled(true);
					manageOfferingButton.setEnabled(true);
					createTourButton.setEnabled(false);
				} else {
					selectedTour = null;
					editTourButton.setEnabled(false);
					manageOfferingButton.setEnabled(false);
					createTourButton.setEnabled(true);
				}
			}
		});

		tourGrid.removeColumn(DB.TOUR_ALLOWED_DAYS_OF_WEEK); // we'll combine days of week and dates
		tourGrid.removeColumn(DB.TOUR_ALLOWED_DATES);

		tourGrid.setColumnOrder(DB.TOUR_ID, DB.TOUR_TOUR_NAME, DB.TOUR_DAYS, DB.TOUR_OFFERING_AVAILABILITY,
				DB.TOUR_DESCRIPTION, DB.TOUR_WEEKDAY_PRICE, DB.TOUR_WEEKEND_PRICE, DB.TOUR_CHILD_DISCOUNT,
				DB.TOUR_TODDLER_DISCOUNT);

		tourGrid.addColumn(tour -> {
			return dbManager.countNumOfferingsForTour(tour);
		}).setId("NUM_OFFERINGS").setCaption("Offering Count");

		HeaderRow filterRow = tourGrid.appendHeaderRow();

		/*
		 * every column has a header, which has a textfield. every textfield is
		 * associated with a value change listener. if the listener detects change, it
		 * adds a filter to the list of filters (gridFilters). if it detects change and
		 * the textfield is empty, it removes from the list of filters.
		 * 
		 * the list of tilers is reapplied everytime on any textfield change.
		 */
		for (Column<Tour, ?> col : tourGrid.getColumns()) {
			col.setMinimumWidth(160);
			col.setHidable(true);
			col.setExpandRatio(1);
			col.setHidingToggleCaption(col.getCaption());
			HeaderCell cell = filterRow.getCell(col.getId());

			// Have an input field to use for filter
			TextField filterField = new TextField();
			filterField.setWidth(130, Unit.PIXELS);
			filterField.setHeight(30, Unit.PIXELS);

			filterField.addValueChangeListener(change -> {
				String searchVal = change.getValue();
				String colId = col.getId();

				log.info("Value change in col [{}], val=[{}]", colId, searchVal);
				ListDataProvider<Tour> dataProvider = (ListDataProvider<Tour>) tourGrid.getDataProvider();

				if (!filterField.isEmpty()) {
					try {
						// note: if we keep typing into same textfield, we will overwrite the old filter
						// for this column, which is desirable (rather than having filters for "h",
						// "he", "hel", etc
						gridFilters.put(colId, FilterFactory.getFilterForTour(colId, searchVal));
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

		this.addComponent(tourGrid);

		// Both buttons should call the same window function now, difference is what is
		// passed to the window
		createTourButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				getUI().getCurrent().addWindow(getSubwindow(tourRepo, tourCollectionCached, new Tour()));
			}

		});

		editTourButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				getUI().getCurrent().addWindow(getSubwindow(tourRepo, tourCollectionCached, selectedTour));
			}

		});

		manageOfferingButton.addClickListener(event -> {
			getUI().getNavigator().navigateTo(OfferingManagementView.VIEW_NAME);
			offeringEditor.setSelectedTour(selectedTour);
			offeringEditor.setTourEditor(this);
		});
	}

	private Window getSubwindow(TourRepository tourRepo, Collection<Tour> tourCollectionCached, Tour tourToSave) {
		// Creating the confirm button
		subwindowConfirm = new Button("Confirm");
		subwindowConfirm.setId("confirm_tour");

		// Creating the fields
		tourName = new TextField("Tour Name");
		tourName.setId("tf_tour_name");
		days = new TextField("Duration (days)");
		days.setId("tf_days");
		tourType = new RadioButtonGroup<String>("Tour Type");
		tourType.setId("rbgrp_tour_type");
		allowedDaysOfWeek = new CheckBoxGroup<String>("Offering Availability");
		allowedDaysOfWeek.setId("chkbxgrp_allowed_days_of_week");
		allowedDates = new TextField("Offering Availability");
		allowedDates.setId("tf_allowed_dates");
		childDiscount = new TextField("Child Discount Multiplier");
		childDiscount.setId("tf_child_discount");
		toddlerDiscount = new TextField("Toddler Discount Multiplier");
		toddlerDiscount.setId("tf_toddler_discount");
		weekdayPrice = new TextField("Weekday Price");
		weekdayPrice.setId("tf_weekday_price");
		weekendPrice = new TextField("Weekend Price");
		weekendPrice.setId("tf_weekend_price");
		descrip = new TextArea("Description");
		descrip.setId("tf_description");

		tourType.setItems(Tour.LIMITED_TOUR_TYPE, Tour.REPEATING_TOUR_TYPE);
		allowedDaysOfWeek.setItems(Utils.getDaysOfWeek());
		tourType.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		allowedDaysOfWeek.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);

		if (tourToSave.getId() == null) { // passed in an unsaved object
			subwindow = new Window("Create new tour");
			allowedDates.setVisible(false);
			tourType.setSelectedItem(Tour.REPEATING_TOUR_TYPE);

		} else { // passed in an object which already exists in db, just need to edit it
			subwindow = new Window("Edit tour");

			if (!tourToSave.getAllowedDaysOfWeek().isEmpty()) { // if it's not a limited type (it's a repeating type)
				for (int day : tourToSave.getAllowedDaysOfWeek()) {
					allowedDaysOfWeek.select(Utils.dayToString(day));
				}
				tourType.setSelectedItem(Tour.REPEATING_TOUR_TYPE);
				allowedDates.setVisible(false);
			} else { // if it's a limited type
				allowedDates.setValue(Utils.dateCollectionToString(tourToSave.getAllowedDates()));
				tourType.setSelectedItem(Tour.LIMITED_TOUR_TYPE);
				allowedDaysOfWeek.setVisible(false);
			}
		}

		FormLayout subContent = new FormLayout();
		subwindow.setWidth("800px");

		subwindow.setContent(subContent);
		subwindow.center();
		subwindow.setClosable(false);
		subwindow.setModal(true);
		subwindow.setResizable(false);
		subwindow.setDraggable(false);

		subContent.addComponent(tourName);
		subContent.addComponent(days);
		subContent.addComponent(tourType);
		subContent.addComponent(allowedDaysOfWeek);
		subContent.addComponent(allowedDates);
		subContent.addComponent(childDiscount);
		subContent.addComponent(toddlerDiscount);
		subContent.addComponent(weekdayPrice);
		subContent.addComponent(weekendPrice);
		subContent.addComponent(descrip);

		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(subwindowConfirm);
		buttonActions.addComponent(new Button("Cancel", event -> subwindow.close()));
		subContent.addComponent(buttonActions);

		tourName.setRequiredIndicatorVisible(true);
		days.setRequiredIndicatorVisible(true);
		tourType.setRequiredIndicatorVisible(true);
		allowedDaysOfWeek.setRequiredIndicatorVisible(true);
		allowedDates.setRequiredIndicatorVisible(true);
		childDiscount.setRequiredIndicatorVisible(true);
		toddlerDiscount.setRequiredIndicatorVisible(true);
		weekdayPrice.setRequiredIndicatorVisible(true);
		weekendPrice.setRequiredIndicatorVisible(true);

		// For the radio button
		tourType.addValueChangeListener(new ValueChangeListener<String>() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				// We need to modify the input method of offering availability
				if (event.getValue().equals(Tour.LIMITED_TOUR_TYPE)) {
					allowedDaysOfWeek.clear();
					allowedDaysOfWeek.setVisible(false);
					allowedDates.setVisible(true);
				} else {
					allowedDates.clear();
					allowedDaysOfWeek.setVisible(true);
					allowedDates.setVisible(false);
				}
			}
		});

		// Binding method according to docs
		Binder<Tour> binder = new Binder<>(Tour.class);

		binder.forField(tourName).withValidator(ValidatorFactory.getStringLengthValidator(255))
				.asRequired(Utils.generateRequiredError()).bind(Tour::getTourName, Tour::setTourName);

		binder.forField(days).withValidator(ValidatorFactory.getIntegerLowerBoundValidator(0))
				.asRequired(Utils.generateRequiredError())
				.withConverter(new StringToIntegerConverter("Must be an integer")).bind(Tour::getDays, Tour::setDays);

		binder.forField(allowedDates).withValidator(ValidatorFactory.getListOfDatesValidator())
				.withConverter(new StringToDateCollectionConverter())
				.bind(Tour::getAllowedDates, Tour::setAllowedDates);

		binder.forField(allowedDaysOfWeek).withConverter(new StringCollectionToIntegerCollectionConverter())
				.bind(Tour::getAllowedDaysOfWeek, Tour::setAllowedDaysOfWeek);

		binder.forField(childDiscount).withValidator(ValidatorFactory.getDoubleRangeValidator(0, 1))
				.asRequired(Utils.generateRequiredError())
				.withConverter(new StringToDoubleConverter("Must be a double"))
				.bind(Tour::getChildDiscount, Tour::setChildDiscount);

		binder.forField(toddlerDiscount).withValidator(ValidatorFactory.getDoubleRangeValidator(0, 1))
				.asRequired(Utils.generateRequiredError())
				.withConverter(new StringToDoubleConverter("Must be a double"))
				.bind(Tour::getToddlerDiscount, Tour::setToddlerDiscount);

		binder.forField(weekdayPrice).withValidator(ValidatorFactory.getIntegerLowerBoundValidator(0))
				.asRequired(Utils.generateRequiredError())
				.withConverter(new StringToIntegerConverter("Must be an integer"))
				.bind(Tour::getWeekdayPrice, Tour::setWeekdayPrice);

		binder.forField(weekendPrice).withValidator(ValidatorFactory.getIntegerLowerBoundValidator(0))
				.asRequired(Utils.generateRequiredError())
				.withConverter(new StringToIntegerConverter("Must be an integer"))
				.bind(Tour::getWeekendPrice, Tour::setWeekendPrice);

		binder.forField(descrip).withValidator(ValidatorFactory.getStringLengthValidator(255))
				.asRequired(Utils.generateRequiredError()).bind(Tour::getDescription, Tour::setDescription);

		// Do set bean to assign value to fields
		binder.setBean(tourToSave);

		subwindowConfirm.addClickListener(event -> {
			BinderValidationStatus<Tour> validationStatus = binder.validate();

			// Special case for tours only to ensure that this field must be filled
			if (allowedDates.isEmpty() && allowedDaysOfWeek.isEmpty()) {
				Notification.show("Could not edit tour, offering availability is required!",
						Notification.TYPE_ERROR_MESSAGE);
			} else if (validationStatus.isOk()) {
				binder.writeBeanIfValid(tourToSave);

				log.info("About to save tour [{}]", tourName.getValue());

				tourRepo.save(tourToSave);
				this.refreshData();
				subwindow.close();
				log.info("created/edited tour [{}] successfully", tourName.getValue());
				binder.removeBean();
			} else {
				StringBuilder stringBuilder = new StringBuilder();

				for (BindingValidationStatus<?> result : validationStatus.getFieldValidationErrors()) {
					if (result.getField() instanceof AbstractField && result.getMessage().isPresent()) {
						stringBuilder.append(((AbstractField) result.getField()).getCaption()).append(" ")
								.append(result.getMessage().get()).append("\n");
					}
				}

				Notification.show("Could not create/edit tour!", stringBuilder.toString(),
						Notification.TYPE_ERROR_MESSAGE);
			}
		});

		return subwindow;
	}

	public interface ChangeHandler {
		void onChange();
	}

	public void refreshData() {
		Iterable<Tour> tours = tourRepo.findAll();
		tourCollectionCached.clear();
		tours.forEach(tourCollectionCached::add);
		ListDataProvider<Tour> provider = new ListDataProvider<Tour>(tourCollectionCached);
		tourGrid.setDataProvider(provider);
		// tourGrid.setItems(tourCollectionCached);
	}

}
