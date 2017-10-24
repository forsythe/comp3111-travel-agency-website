package comp3111.editors;

import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import comp3111.field.HKIDEntryField;
import comp3111.field.PhoneNumberEntryField;
import comp3111.model.Customer;
import comp3111.model.DB;
import comp3111.model.TourGuide;
import comp3111.repo.CustomerRepository;
import comp3111.repo.TourGuideRepository;
import comp3111.validators.Utils;
import comp3111.validators.ValidatorFactory;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
@SpringUI
public class CustomerEditor extends VerticalLayout {
	private static final Logger log = LoggerFactory.getLogger(CustomerEditor.class);

	private Window createCustomerSubwindow;
	private Window editCustomerSubwindow;

	private TextField customerName;
	private TextField customerLineId;
	private HKIDEntryField customerHKID;
	private PhoneNumberEntryField customerPhone;
	private TextField customerAge;

	/* action buttons */
	private HorizontalLayout rowOfButtons = new HorizontalLayout();
	private Button createNewCustomerButton = new Button("Create new customer");
	private Button editCustomerButton = new Button("Edit customer");
	private Button viewCustomerBookingsButton = new Button("View bookings made by customer");

	/* subwindow action buttons */
	private Button subwindowConfirmCreateCustomer;
	private Button subwindowConfirmEditCustomer;

	private Grid<Customer> customersGrid = new Grid<Customer>(Customer.class);

	private Customer selectedCustomer;

	private CustomerRepository customerRepo;
	private final HashSet<Customer> customerCollectionCached = new HashSet<Customer>();
	
	@SuppressWarnings("unchecked")
	@Autowired
	public CustomerEditor(CustomerRepository cr) {
		this.customerRepo = cr;
		
		// adding components
		rowOfButtons.addComponent(createNewCustomerButton);
		rowOfButtons.addComponent(editCustomerButton);
		rowOfButtons.addComponent(viewCustomerBookingsButton);

		// Shouldn't be enabled unless selected
		editCustomerButton.setEnabled(false);
		viewCustomerBookingsButton.setEnabled(false);
	

		// Render component
		this.addComponent(rowOfButtons);

		// Get from DB
		refreshData();

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

		customersGrid.removeColumn(DB.HIBERNATE_NEW_COL);
		customersGrid.removeColumn(DB.CUSTOMER_OFFERINGS);

		customersGrid.setColumnOrder(DB.CUSTOMER_ID, DB.CUSTOMER_NAME, DB.CUSTOMER_LINEID, DB.CUSTOMER_HKID, DB.CUSTOMER_PHONE, DB.CUSTOMER_AGE);

		this.addComponent(customersGrid);

		createNewCustomerButton.addClickListener(event -> {
			getUI().getCurrent().addWindow(getCreateCustomerWindow(customerRepo, customerCollectionCached));
		});
		editCustomerButton.addClickListener(event -> {
			getUI().getCurrent().addWindow(getEditCustomerWindow(customerRepo, customerCollectionCached));
		});
	}

	private Window getCreateCustomerWindow(CustomerRepository customerRepo,
			Collection<Customer> customerCollectionCached) {
		subwindowConfirmCreateCustomer = new Button("Confirm");

		customerName = new TextField("Customer Name");
		customerLineId = new TextField("Customer Line Id");
		customerHKID = new HKIDEntryField("Customer HKID");
		customerPhone = new PhoneNumberEntryField("Phone Number", "852");
		customerAge = new TextField("Customer Age");

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
		subContent.addComponent(customerHKID);
		subContent.addComponent(customerPhone);
		subContent.addComponent(customerAge);

		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(subwindowConfirmCreateCustomer);
		buttonActions.addComponent(new Button("Cancel", event -> createCustomerSubwindow.close()));
		subContent.addComponent(buttonActions);

		Binder<Customer> binder = new Binder<>();
		binder.forField(customerName).withValidator(ValidatorFactory.getStringLengthValidator(255))
				.asRequired(Utils.generateRequiredError()).bind(Customer::getName, Customer::setName);

		binder.forField(customerLineId).withValidator(ValidatorFactory.getStringLengthValidator(255))
				.bind(Customer::getLineId, Customer::setLineId);

		binder.forField(customerHKID).withValidator(ValidatorFactory.getStringLengthValidator(255))
				.withValidator(ValidatorFactory.getHKIDValidator()).asRequired(Utils.generateRequiredError())
				.bind(Customer::getHkid, Customer::setHkid);

		binder.forField(customerPhone).withValidator(ValidatorFactory.getStringLengthValidator(255))
				.withValidator(ValidatorFactory.getPhoneNumberValidator()).asRequired(Utils.generateRequiredError())
				.bind(Customer::getPhone, Customer::setPhone);

		binder.forField(customerAge).withValidator(ValidatorFactory.getIntegerLowerBoundValidator(0))
				.asRequired(Utils.generateRequiredError())
				.withConverter(new StringToIntegerConverter("Must be an integer"))
				.bind(Customer::getAge, Customer::setAge);

		subwindowConfirmCreateCustomer.addClickListener(event -> {
			BinderValidationStatus<Customer> validationStatus = binder.validate();
			log.info(customerHKID.getValue());

			if (validationStatus.isOk()) {
				// Customer must be created by Spring, otherwise it cannot be saved.
				// I do not have access to an empty constructor here
				Customer newCustomer = new Customer();
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
						stringBuilder.append(((AbstractField) result.getField()).getCaption()).append(" ")
								.append(result.getMessage().get()).append("\n");
					}
				}
				Notification.show("Could not create customer!", stringBuilder.toString(),
						Notification.TYPE_ERROR_MESSAGE);
			}
		});
		return createCustomerSubwindow;
	}
	
	private Window getEditCustomerWindow(CustomerRepository customerRepo,
			Collection<Customer> customerCollectionCached) {
		subwindowConfirmEditCustomer = new Button("Confirm");

		customerName = new TextField("Customer Name");
		customerLineId = new TextField("Customer Line Id");
		customerHKID = new HKIDEntryField("Customer HKID");
		customerPhone = new PhoneNumberEntryField("Phone Number", "852");
		customerAge = new TextField("Customer Age");
		
		customerName.setValue(selectedCustomer.getName());
		customerLineId.setValue(selectedCustomer.getLineId());
		//This line is not setting the value, making the hkid blank
		//can't call the func in HKID field as it is protected.
		customerHKID.setValue(selectedCustomer.getHkid());
		customerPhone.setValue(selectedCustomer.getPhone());
		customerAge.setValue(String.valueOf(selectedCustomer.getAge()));

		editCustomerSubwindow = new Window("Edit customer");
		FormLayout subContent = new FormLayout();

		editCustomerSubwindow.setWidth("800px");

		editCustomerSubwindow.setContent(subContent);
		editCustomerSubwindow.center();
		editCustomerSubwindow.setClosable(false);
		editCustomerSubwindow.setModal(true);
		editCustomerSubwindow.setResizable(false);
		editCustomerSubwindow.setDraggable(false);

		subContent.addComponent(customerName);
		subContent.addComponent(customerLineId);
		subContent.addComponent(customerHKID);
		subContent.addComponent(customerPhone);
		subContent.addComponent(customerAge);

		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(subwindowConfirmEditCustomer);
		buttonActions.addComponent(new Button("Cancel", event -> editCustomerSubwindow.close()));
		subContent.addComponent(buttonActions);
		
		Binder<Customer> binder = new Binder<>();
		binder.forField(customerName).withValidator(ValidatorFactory.getStringLengthValidator(255))
				.asRequired(Utils.generateRequiredError()).bind(Customer::getName, Customer::setName);

		binder.forField(customerLineId).withValidator(ValidatorFactory.getStringLengthValidator(255))
				.bind(Customer::getLineId, Customer::setLineId);

		binder.forField(customerHKID).withValidator(ValidatorFactory.getStringLengthValidator(255))
				.withValidator(ValidatorFactory.getHKIDValidator()).asRequired(Utils.generateRequiredError())
				.bind(Customer::getHkid, Customer::setHkid);

		binder.forField(customerPhone).withValidator(ValidatorFactory.getStringLengthValidator(255))
				.withValidator(ValidatorFactory.getPhoneNumberValidator()).asRequired(Utils.generateRequiredError())
				.bind(Customer::getPhone, Customer::setPhone);

		binder.forField(customerAge).withValidator(ValidatorFactory.getIntegerLowerBoundValidator(0))
				.asRequired(Utils.generateRequiredError())
				.withConverter(new StringToIntegerConverter("Must be an integer"))
				.bind(Customer::getAge, Customer::setAge);
		
		subwindowConfirmEditCustomer.addClickListener(event -> {
			BinderValidationStatus<Customer> validationStatus = binder.validate();
			log.info(customerHKID.getValue());

			if (validationStatus.isOk()) {
				// Customer must be created by Spring, otherwise it cannot be saved.
				// I do not have access to an empty constructor here
				binder.writeBeanIfValid(selectedCustomer);

				log.info("About to save customer [{}]", customerName.getValue());

				customerCollectionCached.add(customerRepo.save(selectedCustomer));
				customersGrid.setItems(customerCollectionCached);
				editCustomerSubwindow.close();
				log.info("Saved a new customer [{}] successfully", customerName.getValue());

				refreshData();
				binder.removeBean();
			} else {
				StringBuilder stringBuilder = new StringBuilder();

				for (BindingValidationStatus<?> result : validationStatus.getFieldValidationErrors()) {
					if (result.getField() instanceof AbstractField && result.getMessage().isPresent()) {
						stringBuilder.append(((AbstractField) result.getField()).getCaption()).append(" ")
								.append(result.getMessage().get()).append("\n");
					}
				}
				Notification.show("Could not create customer!", stringBuilder.toString(),
						Notification.TYPE_ERROR_MESSAGE);
			}
		});
		
		return editCustomerSubwindow;
	}

	public interface ChangeHandler {
		void onChange();
	}

	public TextField getCustomerName() {
		return customerName;
	}

	public TextField getCustomerLineId() {
		return customerLineId;
	}

	public HKIDEntryField getCustomerHKID() {
		return customerHKID;
	}

	public PhoneNumberEntryField getCustomerPhone() {
		return customerPhone;
	}

	public TextField getCustomerAge() {
		return customerAge;
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
	
	public void refreshData() {
		Iterable<Customer> customers = customerRepo.findAll();
		//it's possible the customerRepo can return null!
		if (null == customers) {
			customers = new HashSet<Customer>();
		}
		Collection<Customer> customerCollectionCached = new HashSet<Customer>();
		customerCollectionCached.clear();
		customers.forEach(customerCollectionCached::add);
		ListDataProvider<Customer> provider = new ListDataProvider<Customer>(customerCollectionCached);
		customersGrid.setDataProvider(provider);
		// tourGrid.setItems(tourCollectionCached);

	}

}
