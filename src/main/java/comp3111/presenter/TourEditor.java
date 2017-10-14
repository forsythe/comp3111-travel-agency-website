package comp3111.presenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import comp3111.Application;
import comp3111.model.Tour;
import comp3111.repo.TourRepository;
import comp3111.validators.Utils;
import comp3111.validators.ValidatorFactory;

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
	private TextArea description;

	/* Action buttons */
	HorizontalLayout rowOfButtons = new HorizontalLayout();
	private Button createTourButton = new Button("Create new tour");
	private Button editTourButton = new Button("Edit tour");
	private Button manageOfferingButton = new Button("Manage offerings for selected tour");

	/* subwindow action buttons */
	private Button subwindowConfirmCreateTour;

	// Binder<Tour> binder = new Binder<>(Tour.class);

	Grid<Tour> tourGrid = new Grid<Tour>(Tour.class);

	/* The currently edited tour */
	Tour selectedTour;

	@SuppressWarnings("unchecked")
	@Autowired
	public TourEditor(TourRepository tourRepo) {
		// adding components
		rowOfButtons.addComponent(createTourButton);
		rowOfButtons.addComponent(editTourButton);
		rowOfButtons.addComponent(manageOfferingButton);

		// edit and manage shouldn't be enabled with no tour selected
		editTourButton.setEnabled(false);
		manageOfferingButton.setEnabled(false);

		this.addComponent(rowOfButtons);

		// get the repetaingTours from DB
		Iterable<Tour> tours = tourRepo.findAll();
		Collection<Tour> tourCollectionCached = new HashSet<Tour>();
		tours.forEach(tourCollectionCached::add);
		tourGrid.setItems(tourCollectionCached);

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

		tourGrid.removeColumn("new"); // hibernate attributes, we don't care about it
		tourGrid.removeColumn("allowedDaysOfWeek"); // we'll combine into one column
		tourGrid.removeColumn("allowedDates");

		tourGrid.setColumnOrder("id", "tourName", "days", "offeringAvailability", "offerings", "description",
				"weekdayPrice", "weekendPrice", "childDiscount", "toddlerDiscount");
		tourGrid.getColumn("offerings").setWidth(150);

		this.addComponent(tourGrid);

		/************* make the edit tour subwindow **************/
		// TODO

		createTourButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				getUI().getCurrent().addWindow(getCreateTourWindow(tourRepo, tourCollectionCached));
			}

		});

		editTourButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// getUI().getCurrent().addWindow(createTourSubwindow);
			}

		}); // addComponents(getName(), getAge(), actions);

		// bind using naming convention
		// binder.bindInstanceFields(this); can't use this

		// age is a string here, but is an int in Customer.class
		// binder.forField(age).withNullRepresentation("")
		// .withConverter(new StringToIntegerConverter(Integer.valueOf(0), "integers
		// only"))
		// .bind(Customer::getAge, Customer::setAge);
		//
		// binder.forField(name).bind(Customer::getName, Customer::setName);
		//
		// // Configure and style components
		// setSpacing(true);
		// actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		// getSave().setStyleName(ValoTheme.BUTTON_PRIMARY);
		// getSave().setClickShortcut(ShortcutAction.KeyCode.ENTER);
		//
		// // wire action buttons to save, delete and reset
		// getSave().addClickListener(e -> repository.save(customer));
		// getDelete().addClickListener(e -> repository.delete(customer));
		// // cancel.addClickListener(e -> editCustomer(customer));
		// setVisible(false);
	}

	private Window getCreateTourWindow(TourRepository tourRepo, Collection<Tour> tourCollectionCached) {
		subwindowConfirmCreateTour = new Button("Confirm");

		tourName = new TextField("Tour Name");
		days = new TextField("Duration (days)");
		tourType = new RadioButtonGroup<String>("Tour Type");
		allowedDaysOfWeek = new CheckBoxGroup<String>("Offering Availability");
		allowedDates = new TextField("Offering Availability");
		childDiscount = new TextField("Child Discount Multiplier");
		toddlerDiscount = new TextField("Toddler Discount Multiplier");
		weekdayPrice = new TextField("Weekday Price");
		weekendPrice = new TextField("Weekend Price");
		description = new TextArea("Description");

		FormLayout subContent = new FormLayout();
		Window createTourSubwindow = new Window("Create new tour");
		createTourSubwindow.setWidth("40%");
		createTourSubwindow.setContent(subContent);
		createTourSubwindow.center();
		createTourSubwindow.setClosable(false);
		createTourSubwindow.setModal(true);
		createTourSubwindow.setResizable(false);
		createTourSubwindow.setDraggable(false);

		subContent.addComponent(tourName);
		subContent.addComponent(days);
		subContent.addComponent(tourType);
		subContent.addComponent(allowedDaysOfWeek);
		subContent.addComponent(allowedDates);
		subContent.addComponent(childDiscount);
		subContent.addComponent(toddlerDiscount);
		subContent.addComponent(weekdayPrice);
		subContent.addComponent(weekendPrice);
		subContent.addComponent(description);

		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(subwindowConfirmCreateTour);
		buttonActions.addComponent(new Button("Cancel", event -> createTourSubwindow.close()));
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

		tourType.setItems("Limited", "Repeating");
		tourType.setSelectedItem("Repeating");
		allowedDaysOfWeek.setItems("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
		allowedDates.setVisible(false);
		tourType.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		allowedDaysOfWeek.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);

		tourType.addValueChangeListener(new ValueChangeListener<String>() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event.getValue().equals("Limited")) {
					allowedDaysOfWeek.setVisible(false);
					allowedDates.setVisible(true);
				} else {
					allowedDaysOfWeek.setVisible(true);
					allowedDates.setVisible(false);
				}
			}
		});

		Utils.addValidator(days, ValidatorFactory.getIntegerLowerBoundValidator(0));
		Utils.addValidator(allowedDates, ValidatorFactory.getListOfDatesValidator());
		Utils.addValidator(childDiscount, ValidatorFactory.getDoubleRangeValidator(0, 1));
		Utils.addValidator(toddlerDiscount, ValidatorFactory.getDoubleRangeValidator(0, 1));
		Utils.addValidator(weekdayPrice, ValidatorFactory.getIntegerLowerBoundValidator(0));
		Utils.addValidator(weekendPrice, ValidatorFactory.getIntegerLowerBoundValidator(0));

		subwindowConfirmCreateTour.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				boolean isRepeatingTourType = tourType.getValue().equals("Repeating");
				ArrayList<String> errorMsgs = new ArrayList<String>();
				ArrayList<TextField> nonNullableComponents = new ArrayList<TextField>();
				nonNullableComponents.addAll(
						Arrays.asList(tourName, days, childDiscount, toddlerDiscount, weekdayPrice, weekendPrice));

				if (!isRepeatingTourType) {
					nonNullableComponents.add(allowedDates);
				}

				for (TextField field : nonNullableComponents) {
					if (field.isEmpty()) {
						log.info(field.getCaption() + ": cannot be empty");
						errorMsgs.add(field.getCaption() + ": cannot be empty");
					}
				}

				if (isRepeatingTourType && allowedDaysOfWeek.getSelectedItems().size() == 0) {
					errorMsgs.add(allowedDaysOfWeek.getCaption() + ": Please select at least one day");
				}

				ArrayList<TextField> fieldsWithValidators = new ArrayList<TextField>();
				fieldsWithValidators.addAll(
						Arrays.asList(days, allowedDates, childDiscount, toddlerDiscount, weekdayPrice, weekendPrice));

				for (TextField field : fieldsWithValidators) {
					if (field.getErrorMessage() != null) {
						log.info(field.getCaption() + ": " + field.getErrorMessage().toString());
						errorMsgs.add(field.getCaption() + ": " + field.getErrorMessage().toString());
					}
				}
				log.info("errorMsgs.size() is [{}]", errorMsgs.size());

				if (errorMsgs.size() == 0) {
					Tour newTour = new Tour(tourName.getValue(), description.getValue(),
							Integer.parseInt(days.getValue()), Double.parseDouble(childDiscount.getValue()),
							Double.parseDouble(toddlerDiscount.getValue()), Integer.parseInt(weekdayPrice.getValue()),
							Integer.parseInt(weekendPrice.getValue()));

					if (isRepeatingTourType) {
						newTour.setAllowedDaysOfWeek(Utils.stringDayNameSetToIntegerSet(allowedDaysOfWeek.getValue()));
					} else {
						newTour.setAllowedDates(Utils.stringToDateSet(allowedDates.getValue()));
					}

					log.info("Saved a new tour [{}] successfully", tourName.getValue());
					tourName.clear();
					days.clear();
					allowedDaysOfWeek.deselectAll();
					allowedDates.clear();
					childDiscount.clear();
					toddlerDiscount.clear();
					weekdayPrice.clear();
					weekendPrice.clear();
					description.clear();
					/* bug in vaadin: need to force the grid to update cosmetically */
					tourCollectionCached.add(tourRepo.save(newTour));
					tourGrid.setItems(tourCollectionCached);
					createTourSubwindow.close();
				} else {
					String errorString = "";
					for (String err : errorMsgs) {
						errorString += err + "\n";
					}
					Notification.show("Could not create tour!", errorString, Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});
		return createTourSubwindow;
	}

	public interface ChangeHandler {
		void onChange();
	}

	// public final void editCustomer(Customer c) {
	// if (c == null) {
	// setVisible(false);
	// return;
	// }
	// final boolean persisted = c.getId() != null;
	// if (persisted) {
	// // Find fresh entity for editing
	// customer = repository.findOne(c.getId());
	// } else {
	// customer = c;
	// }
	// cancel.setVisible(persisted);
	//
	// // Bind customer properties to similarly named fields
	// // Could also use annotation or "manual binding" or programmatically
	// // moving values from fields to entities before saving
	// binder.setBean(customer);
	//
	// setVisible(true);
	//
	// // A hack to ensure the whole form is visible
	// getSave().focus();
	// // Select all text in firstName field automatically
	// getName().selectAll();
	// }

	// public void setChangeHandler(ChangeHandler h) {
	// // ChangeHandler is notified when either save or delete
	// // is clicked
	// getSave().addClickListener(e -> h.onChange());
	// getDelete().addClickListener(e -> h.onChange());
	// }

	public TextField getName() {
		return tourName;
	}

	public void setName(TextField name) {
		this.tourName = name;
	}

	public Button getCreateTourButton() {
		return createTourButton;
	}

	public void setCreateTourButton(Button createTourButton) {
		this.createTourButton = createTourButton;
	}

	public Button getEditTourButton() {
		return editTourButton;
	}

	public void setEditTourButton(Button editTourButton) {
		this.editTourButton = editTourButton;
	}

	public Button getManageOfferingButton() {
		return manageOfferingButton;
	}

	public void setManageOfferingButton(Button manageOfferingButton) {
		this.manageOfferingButton = manageOfferingButton;
	}
}
