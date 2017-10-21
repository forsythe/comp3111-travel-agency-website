package comp3111.presenter;

import java.util.Collection;
import java.util.HashSet;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.converter.StringToIntegerConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Grid.SelectionMode;

import comp3111.model.Customer;
import comp3111.repo.CustomerRepository;
import comp3111.validators.Utils;
import comp3111.validators.ValidatorFactory;
import comp3111.field.PhoneNumberEntryField;
import comp3111.field.HKIDEntryField;

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
	private static final Logger log = LoggerFactory.getLogger(CustomerEditor.class);
	
	private Window createCustomerSubwindow;
	
	/* action buttons */
	private HorizontalLayout rowOfButtons = new HorizontalLayout();
	private Button createNewCustomerButton = new Button("Create new customer");
	private Button editCustomerButton = new Button("Edit customer");
	private Button viewCustomerBookingsButton = new Button("View bookings made by customer");
	
	/* subwindow action buttons */
	private Button subwindowConfirmCreateCustomer;

	private Grid<Customer> customersGrid = new Grid<Customer>(Customer.class);

	private Customer selectedCustomer;
	
	@SuppressWarnings("unchecked")
	@Autowired
	public CustomerEditor(CustomerRepository customerRepo) {
		// adding components
		rowOfButtons.addComponent(createNewCustomerButton);
		rowOfButtons.addComponent(editCustomerButton);
		rowOfButtons.addComponent(viewCustomerBookingsButton);
		
		//Shouldn't be enabled unless selected
		editCustomerButton.setEnabled(false);
		viewCustomerBookingsButton.setEnabled(false);
		
		//Render component
		this.addComponent(rowOfButtons);
		
		//Get from DB
		Iterable<Customer> customers = customerRepo.findAll();
		Collection<Customer> customerCollectionCached = new HashSet<Customer>();
		customers.forEach(customerCollectionCached::add);
		customersGrid.setItems(customerCollectionCached);

		customersGrid.setWidth("100%");
		customersGrid.setSelectionMode(SelectionMode.SINGLE);
		
		customersGrid.addSelectionListener(event -> {
			if (event.getFirstSelectedItem().isPresent()) {
				selectedCustomer = event.getFirstSelectedItem().get();

				editCustomerButton.setEnabled(true);
				viewCustomerBookingsButton.setEnabled(true);
				createNewCustomerButton.setEnabled(false);
			} else {
				selectedCustomer = null;

				editCustomerButton.setEnabled(false);
				viewCustomerBookingsButton.setEnabled(false);
				createNewCustomerButton.setEnabled(true);
			}
		});
		
		customersGrid.removeColumn("new");
		customersGrid.removeColumn("customerOffering");
		
		customersGrid.setColumnOrder("id", "name", "lineId", "hkid", "phone", "age");
		
		this.addComponent(customersGrid);
		
		createNewCustomerButton.addClickListener(event -> {
			getUI().getCurrent().addWindow(getCreateCustomerWindow(customerRepo, customerCollectionCached));
		});
	}
	
	private Window getCreateCustomerWindow(CustomerRepository customerRepo, Collection<Customer> customerCollectionCached) {
		subwindowConfirmCreateCustomer = new Button("Confirm");
		
		TextField customerName = new TextField("Customer Name");
		TextField customerLineId = new TextField("Customer Line Id");
		HKIDEntryField customerHkid = new HKIDEntryField("Customer HKID");
		PhoneNumberEntryField customerPhone = new PhoneNumberEntryField("Phone Number");
		TextField customerAge = new TextField("Customer Age");
		
		createCustomerSubwindow = new Window("Create new customer");
		FormLayout subContent = new FormLayout();
		
		createCustomerSubwindow.setWidth("800px");
		
		createCustomerSubwindow.setContent(subContent);
		createCustomerSubwindow.center();
		createCustomerSubwindow.setClosable(false);
		createCustomerSubwindow.setModal(true);
		createCustomerSubwindow.setResizable(false);
		createCustomerSubwindow.setDraggable(false);
		
		subContent.addComponent(customerName);
		subContent.addComponent(customerLineId);
		subContent.addComponent(customerHkid);
		subContent.addComponent(customerPhone);
		subContent.addComponent(customerAge);
		
		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(subwindowConfirmCreateCustomer);
		buttonActions.addComponent(new Button("Cancel", event -> createCustomerSubwindow.close()));
		subContent.addComponent(buttonActions);

		Binder<Customer> binder = new Binder<>();
		binder.forField(customerName)
				.withValidator(ValidatorFactory.getStringLengthValidator(255))
				.asRequired(Utils.generateRequiredError())
				.bind(Customer::getName, Customer::setName);

		binder.forField(customerLineId)
				.withValidator(ValidatorFactory.getStringLengthValidator(255))
				.bind(Customer::getLineId, Customer::setLineId);

		binder.forField(customerHkid)
				.withValidator(ValidatorFactory.getStringLengthValidator(255))
				.withValidator(ValidatorFactory.getHKIDValidator())
				.asRequired(Utils.generateRequiredError())
				.bind(Customer::getHkid, Customer::setHkid);

		binder.forField(customerPhone)
				.withValidator(ValidatorFactory.getStringLengthValidator(255))
				.withValidator(ValidatorFactory.getPhoneNumberValidator())
				.asRequired(Utils.generateRequiredError())
				.bind(Customer::getPhone, Customer::setPhone);

		binder.forField(customerAge)
				.withValidator(ValidatorFactory.getIntegerLowerBoundValidator(0))
				.asRequired(Utils.generateRequiredError())
				.withConverter(new StringToIntegerConverter("Must be an integer"))
				.bind(Customer::getAge, Customer::setAge);

		subwindowConfirmCreateCustomer.addClickListener(event -> {
			BinderValidationStatus<Customer> validationStatus = binder.validate();
			log.info(customerHkid.getValue());

			if (validationStatus.isOk()) {
				//Customer must be created by Spring, otherwise it cannot be saved.
				//I do not have access to an empty constructor here
				Customer newCustomer = new Customer("foo", 999);
				binder.writeBeanIfValid(newCustomer);

				log.info("About to save customer [{}]", customerName.getValue());

				customerCollectionCached.add(customerRepo.save(newCustomer));
				customersGrid.setItems(customerCollectionCached);
				createCustomerSubwindow.close();
				log.info("Saved a new customer [{}] successfully", customerName.getValue());

				binder.removeBean();
			} else {
				StringBuilder stringBuilder = new StringBuilder();

				for (BindingValidationStatus<?> result : validationStatus.getFieldValidationErrors()) {
					if (result.getField() instanceof AbstractField && result.getMessage().isPresent()) {
						stringBuilder.append(((AbstractField) result.getField()).getCaption())
								.append(" ")
								.append(result.getMessage().get())
								.append("\n");
					}
				}
				Notification.show("Could not create customer!", stringBuilder.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
		});
		return createCustomerSubwindow;
	}
	
	public interface ChangeHandler {
		void onChange();
	}
	
	public Window getCreateCustomerSubwindow() {
		return createCustomerSubwindow;
	}

	public Button getCreateNewCustomerButton() {
		return createNewCustomerButton;
	}

	public Button getEditCustomerButton() {
		return editCustomerButton;
	}

	public Button getViewCustomerBookingsButton() {
		return viewCustomerBookingsButton;
	}

	public Button getSubwindowConfirmCreateCustomer() {
		return subwindowConfirmCreateCustomer;
	}

}
