package comp3111.presenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.SelectionModel;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.GridSelectionModel;
import com.vaadin.ui.components.grid.SingleSelectionModel;
import com.vaadin.ui.themes.ValoTheme;

import comp3111.model.Customer;
import comp3111.model.LimitedTour;
import comp3111.model.RepeatingTour;
import comp3111.model.Tour;
import comp3111.repo.CustomerRepository;
import comp3111.repo.LimitedTourRepository;
import comp3111.repo.RepeatingTourRepository;
import comp3111.repo.TourBaseRepository;

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

	/* Fields to edit properties in Tour entity */
	private TextField name = new TextField("name");
	private TextField age = new TextField("age");

	/* Action buttons */
	HorizontalLayout rowOfButtons = new HorizontalLayout();
	private Button createTourButton = new Button("Create new tour");
	private Button editTourButton = new Button("Edit tour");
	private Button manageOfferingButton = new Button("Manage offering for selected tour");

	// CssLayout actions = new CssLayout(getSave(), cancel, getDelete());

	Binder<Tour> binder = new Binder<>(Tour.class);

	Grid<RepeatingTour> grid = new Grid(Tour.class);

	@Autowired
	public TourEditor(RepeatingTourRepository repository) {
		// adding components
		rowOfButtons.addComponent(createTourButton);
		rowOfButtons.addComponent(editTourButton);
		rowOfButtons.addComponent(manageOfferingButton);

		// edit and manage shouldn't be enabled with no tour selected
		editTourButton.setEnabled(false);
		manageOfferingButton.setEnabled(false);

		this.addComponent(rowOfButtons);

		// get the repetaingTours from DB
		Iterable<RepeatingTour> tours = repository.findAll();
		Collection<RepeatingTour> toursCollection = new ArrayList<RepeatingTour>();
		tours.forEach(toursCollection::add);
		grid.setItems(toursCollection);
		
		
		grid.setWidth("100%");
		grid.setSelectionMode(SelectionMode.SINGLE);
		GridSelectionModel<RepeatingTour> selection = grid.getSelectionModel();

		grid.addSelectionListener(new SelectionListener<RepeatingTour>() {
			@Override
			public void selectionChange(SelectionEvent event) {
				Collection<RepeatingTour> selectedItems = selection.getSelectedItems();
				RepeatingTour selectedTour = null;
				for (RepeatingTour rt : selectedItems) { // easy way to get first element of set
					selectedTour = rt;
					break;
				}
				if (selectedTour != null) {
					editTourButton.setEnabled(true);
					manageOfferingButton.setEnabled(true);
					createTourButton.setEnabled(false);
				} else {
					editTourButton.setEnabled(true);
					manageOfferingButton.setEnabled(true);
					createTourButton.setEnabled(true);
				}
			}

		});

		this.addComponent(grid);

		// addComponents(getName(), getAge(), actions);

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
		return name;
	}

	public void setName(TextField name) {
		this.name = name;
	}

	public TextField getAge() {
		return age;
	}

	public void setAge(TextField age) {
		this.age = age;
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
