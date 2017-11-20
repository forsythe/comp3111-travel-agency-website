package comp3111.input.editors;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.Page;
import com.vaadin.server.UserError;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

import comp3111.Utils;
import comp3111.data.DBManager;
import comp3111.data.GridCol;
import comp3111.data.model.Tour;
import comp3111.data.repo.TourRepository;
import comp3111.input.converters.ConverterFactory;
import comp3111.input.converters.StringCollectionToIntegerCollectionConverter;
import comp3111.input.converters.StringToDateCollectionConverter;
import comp3111.input.validators.ValidatorFactory;
import comp3111.view.NotificationFactory;
import comp3111.view.OfferingManagementView;

/**
 * Represents the tour editor in TourManagementView
 * 
 * @author Forsythe
 *
 */
@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class TourEditor extends VerticalLayout {
	private static final Logger log = LoggerFactory.getLogger(TourEditor.class);

	private Window subwindow;
	private DBManager dbManager;
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
	private RadioButtonGroup<String> isChildFriendly;
	private BinderValidationStatus<Tour> validationStatus;

	/* Action buttons */
	private HorizontalLayout rowOfButtons = new HorizontalLayout();
	private Button createTourButton = new Button("Create new tour");

	private Button editTourButton = new Button("Edit tour");
	private Button manageOfferingButton = new Button("Manage offerings for selected tour");
	/* subwindow action buttons */
	private Button subwindowConfirmButton;

	// this is FINAL so we can access it inside our filtering callback function
	private final Grid<Tour> grid = new Grid<Tour>(Tour.class);

	/* The currently edited tour */
	Tour selectedTour;

	TourRepository tourRepo;

	// final just means we cant rebind the var name. we can still add/remove
	// IMPORTANT: this is FINAL so we can access it inside the filtering callback
	// function
	private final HashSet<Tour> tourCollectionCached = new HashSet<Tour>();

	public HashSet<Tour> getTourCollectionCached() {
		return tourCollectionCached;
	}

	// the set of filters to apply on our table
	private final HashMap<String, ProviderAndPredicate<?, ?>> gridFilters = new HashMap<String, ProviderAndPredicate<?, ?>>();

	/**
	 * Constructs the editor for creating/editing tours
	 * 
	 * @param tr
	 *            The TourRepository
	 * @param dbm
	 *            The DBManager
	 * @param ofe
	 *            The OfferingEditor
	 */
	@SuppressWarnings("unchecked")
	@Autowired
	public TourEditor(TourRepository tr, DBManager dbm, OfferingEditor ofe) {

		this.tourRepo = tr;
		this.dbManager = dbm;
		this.offeringEditor = ofe;

		// adding components
		rowOfButtons.addComponent(createTourButton);
		rowOfButtons.addComponent(editTourButton);
		rowOfButtons.addComponent(manageOfferingButton);
		createTourButton.setId("btn_create_tour");
		editTourButton.setId("btn_edit_tour");
		manageOfferingButton.setId("btn_manage_tour_offerings");

		// edit and manage shouldn't be enabled with no tour selected
		editTourButton.setEnabled(false);
		manageOfferingButton.setEnabled(false);

		this.addComponent(rowOfButtons);

		// get the repetaingTours from GridCol
		this.refreshData();

		grid.setWidth("100%");
		grid.setSelectionMode(SelectionMode.SINGLE);

		grid.addSelectionListener(event -> {
			{
				if (event.getFirstSelectedItem().isPresent()) {
					selectedTour = event.getFirstSelectedItem().get();
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

		grid.removeColumn(GridCol.TOUR_ALLOWED_DAYS_OF_WEEK); // we'll combine days of week and dates
		grid.removeColumn(GridCol.TOUR_ALLOWED_DATES);

		grid.setColumnOrder(GridCol.TOUR_ID, GridCol.TOUR_TOUR_NAME, GridCol.TOUR_DAYS,
				GridCol.TOUR_OFFERING_AVAILABILITY, GridCol.TOUR_DESCRIPTION, GridCol.TOUR_WEEKDAY_PRICE,
				GridCol.TOUR_WEEKEND_PRICE, GridCol.TOUR_CHILD_DISCOUNT, GridCol.TOUR_TODDLER_DISCOUNT,
				GridCol.TOUR_IS_CHILD_FRIENDLY);

		FilterFactory.addFilterInputBoxesToGridHeaderRow(Tour.class, grid, gridFilters);

		this.addComponent(grid);

		// Both buttons should call the same window function now, difference is what is
		// passed to the window
		createTourButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				getUI();
				UI.getCurrent().addWindow(getSubwindow(tourRepo, tourCollectionCached, new Tour()));
			}

		});

		editTourButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				getUI();
				UI.getCurrent().addWindow(getSubwindow(tourRepo, tourCollectionCached, selectedTour));
			}

		});

		manageOfferingButton.addClickListener(event -> {
			offeringEditor.setSelectedTour(selectedTour);
			offeringEditor.setTourEditor(this);
			getUI().getNavigator().navigateTo(OfferingManagementView.VIEW_NAME);
			refreshData();
		});
	}

	/**
	 * Creates the popup window for creating/editing tours
	 * 
	 * @param tourRepo
	 *            The tour repository
	 * @param tourCollectionCached
	 *            A local cached version of the tours
	 * @param tourToSave
	 *            The transient or detached tour to save
	 * @return The window
	 */
	public Window getSubwindow(TourRepository tourRepo, Collection<Tour> tourCollectionCached, Tour tourToSave) {
		// Creating the confirm button
		subwindowConfirmButton = new Button("Confirm");
		getSubwindowConfirmButton().setId("btn_confirm_tour");

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
		isChildFriendly = new RadioButtonGroup<String>("Child Friendly");
		isChildFriendly.setId("rbgrp_is_child_friendly");

		descrip = new TextArea("Description");
		descrip.setId("tf_description");

		tourType.setItems(Tour.LIMITED_TOUR_TYPE, Tour.REPEATING_TOUR_TYPE);
		allowedDaysOfWeek.setItems(Utils.getDaysOfWeek());
		tourType.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		allowedDaysOfWeek.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);

		isChildFriendly.setItems("true", "false");
		isChildFriendly.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);

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
		VerticalLayout formContainer = new VerticalLayout();
		formContainer.addComponent(subContent);

		subwindow.setWidth("800px");
		subwindow.setContent(formContainer);

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
		subContent.addComponent(isChildFriendly);
		subContent.addComponent(descrip);

		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(getSubwindowConfirmButton());
		buttonActions.addComponent(new Button("Cancel", event -> subwindow.close()));
		subContent.addComponent(buttonActions);

		// the other fields get their indicators from asRequired()
		// However, these two cannot because only ONE of them can be filled
		allowedDaysOfWeek.setRequiredIndicatorVisible(true);
		allowedDates.setRequiredIndicatorVisible(true);

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

		binder.forField(days).asRequired(Utils.generateRequiredError())
				.withConverter(ConverterFactory.getStringToIntegerConverter())
				.withValidator(ValidatorFactory.getIntegerRangeValidator(1)).bind(Tour::getDays, Tour::setDays);

		binder.forField(allowedDates).withValidator(ValidatorFactory.getListOfDatesValidator())
				.withConverter(new StringToDateCollectionConverter())
				.bind(Tour::getAllowedDates, Tour::setAllowedDates);

		binder.forField(allowedDaysOfWeek).withConverter(new StringCollectionToIntegerCollectionConverter())
				.bind(Tour::getAllowedDaysOfWeek, Tour::setAllowedDaysOfWeek);

		binder.forField(childDiscount).asRequired(Utils.generateRequiredError())
				.withConverter(ConverterFactory.getStringToDoubleConverter())
				.withValidator(ValidatorFactory.getDoubleRangeValidator(0, 1))
				.bind(Tour::getChildDiscount, Tour::setChildDiscount);

		binder.forField(toddlerDiscount).asRequired(Utils.generateRequiredError())
				.withConverter(ConverterFactory.getStringToDoubleConverter())
				.withValidator(ValidatorFactory.getDoubleRangeValidator(0, 1))
				.bind(Tour::getToddlerDiscount, Tour::setToddlerDiscount);

		binder.forField(weekdayPrice).asRequired(Utils.generateRequiredError())
				.withConverter(ConverterFactory.getStringToIntegerConverter())
				.withValidator(ValidatorFactory.getIntegerRangeValidator(0))
				.bind(Tour::getWeekdayPrice, Tour::setWeekdayPrice);

		binder.forField(weekendPrice).asRequired(Utils.generateRequiredError())
				.withConverter(ConverterFactory.getStringToIntegerConverter())
				.withValidator(ValidatorFactory.getIntegerRangeValidator(0))
				.bind(Tour::getWeekendPrice, Tour::setWeekendPrice);

		binder.forField(descrip).withValidator(ValidatorFactory.getStringLengthValidator(255))
				.asRequired(Utils.generateRequiredError()).bind(Tour::getDescription, Tour::setDescription);

		binder.forField(isChildFriendly).asRequired(Utils.generateRequiredError())
				.withConverter(ConverterFactory.getStringToBooleanConverter())
				.bind(Tour::isChildFriendly, Tour::setChildFriendly);

		// Do set bean to assign value to fields
		binder.setBean(tourToSave);

		getSubwindowConfirmButton().addClickListener(event -> {
			validationStatus = binder.validate();

			// Special case for tours only to ensure that this field must be filled
			if (validationStatus.isOk() && !(allowedDates.isEmpty() && allowedDaysOfWeek.isEmpty())) {
				binder.writeBeanIfValid(tourToSave);

				log.info("About to save tour [{}]", tourName.getValue());

				tourRepo.save(tourToSave);
				this.refreshData();
				subwindow.close();
				log.info("created/edited tour [{}] successfully", tourName.getValue());
				if (Page.getCurrent() != null) // can be null in mockito testing
					NotificationFactory.getTopBarSuccessNotification().show(Page.getCurrent());

				binder.removeBean();
			} else {

				String errors = ValidatorFactory.getValidatorErrorsString(validationStatus);
				if (allowedDates.isEmpty() && allowedDaysOfWeek.isEmpty()) {
					errors += "[Offering Availability] cannot be empty\n";
					if (tourType.getValue().equals(Tour.LIMITED_TOUR_TYPE)) {
						allowedDates.setComponentError(new UserError(Utils.generateRequiredError()));
					} else {
						allowedDaysOfWeek.setComponentError(new UserError(Utils.generateRequiredError()));
					}
				}
				if (Page.getCurrent() != null) // may be null during mockito testing
					NotificationFactory.getTopBarWarningNotification(errors, 5).show(Page.getCurrent());

			}
		});

		return subwindow;
	}

	/**
	 * Refreshes the data in the vaadin grid
	 */
	public void refreshData() {

		Iterable<Tour> tours = tourRepo.findAll();

		if (tours != null) {
			tourCollectionCached.clear();
			tours.forEach(tourCollectionCached::add);
			ListDataProvider<Tour> provider = new ListDataProvider<Tour>(tourCollectionCached);
			grid.setDataProvider(provider);
		}

		// grid.setItems(tourCollectionCached);
	}

	public Button getSubwindowConfirmButton() {
		return subwindowConfirmButton;
	}

	/**
	 * @return the tourName field
	 */
	public TextField getTourName() {
		return tourName;
	}

	/**
	 * @return the days field
	 */
	public TextField getDays() {
		return days;
	}

	/**
	 * @return the tourType field
	 */
	public RadioButtonGroup<String> getTourType() {
		return tourType;
	}

	/**
	 * @return the allowedDaysOfWeek field
	 */
	public CheckBoxGroup<String> getAllowedDaysOfWeek() {
		return allowedDaysOfWeek;
	}

	/**
	 * @return the allowedDates field
	 */
	public TextField getAllowedDates() {
		return allowedDates;
	}

	/**
	 * @return the childDiscount field
	 */
	public TextField getChildDiscount() {
		return childDiscount;
	}

	/**
	 * @return the toddlerDiscount field
	 */
	public TextField getToddlerDiscount() {
		return toddlerDiscount;
	}

	/**
	 * @return the weekdayPrice field
	 */
	public TextField getWeekdayPrice() {
		return weekdayPrice;
	}

	/**
	 * @return the weekendPrice field
	 */
	public TextField getWeekendPrice() {
		return weekendPrice;
	}

	/**
	 * @return the descrip field
	 */
	public TextArea getDescrip() {
		return descrip;
	}

	/**
	 * @return the isChildFriendly field
	 */
	public RadioButtonGroup<String> getIsChildFriendly() {
		return isChildFriendly;
	}

	/**
	 * @return the validationStatus
	 */
	public BinderValidationStatus<Tour> getValidationStatus() {
		return validationStatus;
	}
}
