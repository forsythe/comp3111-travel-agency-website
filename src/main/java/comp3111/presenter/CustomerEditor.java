package comp3111.presenter;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import comp3111.model.Customer;
import comp3111.repo.CustomerRepository;

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
public class CustomerEditor extends VerticalLayout {

	private final CustomerRepository repository;

	/**
	 * The currently edited customer
	 */
	private Customer customer;

	/* Fields to edit properties in Customer entity */
	private TextField name = new TextField("name");
	private TextField age = new TextField("age");

	/* Action buttons */
	private Button save = new Button("Save", FontAwesome.SAVE);
	Button cancel = new Button("Cancel");
	private Button delete = new Button("Delete", FontAwesome.TRASH_O);

	CssLayout actions = new CssLayout(getSave(), cancel, getDelete());

	Binder<Customer> binder = new Binder<>(Customer.class);

	@Autowired
	public CustomerEditor(CustomerRepository repository) {
		this.repository = repository;

		addComponents(getName(), getAge(), actions);

		// bind using naming convention
		//binder.bindInstanceFields(this); can't use this

		// age is a string here, but is an int in Customer.class
		binder.forField(age).withNullRepresentation("")
				.withConverter(new StringToIntegerConverter(Integer.valueOf(0), "integers only"))
				.bind(Customer::getAge, Customer::setAge);
		
		binder.forField(name).bind(Customer::getName, Customer::setName);

		// Configure and style components
		setSpacing(true);
		actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		getSave().setStyleName(ValoTheme.BUTTON_PRIMARY);
		getSave().setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to save, delete and reset
		getSave().addClickListener(e -> repository.save(customer));
		getDelete().addClickListener(e -> repository.delete(customer));
		cancel.addClickListener(e -> editCustomer(customer));
		setVisible(false);
	}

	public interface ChangeHandler {

		void onChange();
	}

	public final void editCustomer(Customer c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = c.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			customer = repository.findOne(c.getId());
		} else {
			customer = c;
		}
		cancel.setVisible(persisted);

		// Bind customer properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(customer);

		setVisible(true);

		// A hack to ensure the whole form is visible
		getSave().focus();
		// Select all text in firstName field automatically
		getName().selectAll();
	}

	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either save or delete
		// is clicked
		getSave().addClickListener(e -> h.onChange());
		getDelete().addClickListener(e -> h.onChange());
	}

	public TextField getName() {
		return name;
	}

	public void setFirstName(TextField firstName) {
		this.name = firstName;
	}

	public TextField getAge() {
		return age;
	}

	public void setAge(TextField age) {
		this.age = age;
	}

	public Button getSave() {
		return save;
	}

	public void setSave(Button save) {
		this.save = save;
	}

	public Button getDelete() {
		return delete;
	}

	public void setDelete(Button delete) {
		this.delete = delete;
	}

}
