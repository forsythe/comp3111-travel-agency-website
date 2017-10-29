package comp3111.input.editors;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;

import comp3111.Utils;
import comp3111.data.DB;
import comp3111.data.model.Customer;
import comp3111.data.model.TourGuide;
import comp3111.data.repo.CustomerRepository;
import comp3111.input.field.HKIDEntryField;
import comp3111.input.field.PhoneNumberEntryField;
import comp3111.input.validators.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashSet;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
@SpringUI
public class CustomerEditor extends VerticalLayout {
	private static final Logger log = LoggerFactory.getLogger(CustomerEditor.class);

	private Window subwindow;

	/* action buttons */
	private Button createNewCustomerButton = new Button("Create new customer");
	private Button editCustomerButton = new Button("Edit customer");
	private Button viewCustomerBookingsButton = new Button("View bookings made by customer");

	private Grid<Customer> customersGrid = new Grid<Customer>(Customer.class);

	private Customer selectedCustomer;

	private CustomerRepository customerRepo;
	private final HashSet<Customer> customerCollectionCached = new HashSet<Customer>();

	@Autowired
	public CustomerEditor(CustomerRepository cr) {
		this.customerRepo = cr;

		// adding components
		HorizontalLayout rowOfButtons = new HorizontalLayout();

		rowOfButtons.addComponent(createNewCustomerButton);
		rowOfButtons.addComponent(editCustomerButton);
		rowOfButtons.addComponent(viewCustomerBookingsButton);
		createNewCustomerButton.setId("button_create_customer");
		editCustomerButton.setId("button_edit_customer");
		viewCustomerBookingsButton.setId("button_view_customer_bookings");

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


		customersGrid.setColumnOrder(DB.CUSTOMER_ID, DB.CUSTOMER_NAME, DB.CUSTOMER_LINEID, DB.CUSTOMER_HKID,
				DB.CUSTOMER_PHONE, DB.CUSTOMER_AGE);

//		HeaderRow filterRow = customersGrid.appendHeaderRow();
		
		for (Column<Customer, ?> col : customersGrid.getColumns()) {
			col.setMinimumWidth(120);
			col.setHidable(true);
			col.setHidingToggleCaption(col.getCaption());
			col.setExpandRatio(1);
		}
		
		this.addComponent(customersGrid);

		createNewCustomerButton.addClickListener(event -> {
			getUI().getCurrent().addWindow(getSubwindow(customerRepo, customerCollectionCached, new Customer()));
		});
		editCustomerButton.addClickListener(event -> {
			getUI().getCurrent().addWindow(getSubwindow(customerRepo, customerCollectionCached, selectedCustomer));
		});
	}

	private Window getSubwindow(CustomerRepository customerRepo, Collection<Customer> customerCollectionCached,
			Customer customerToSave) {
		// Creating the confirm button
		Button subwindowConfirm = new Button("Confirm");
		subwindowConfirm.setId("confirm_customer");

		TextField customerName = new TextField("Customer Name");
		customerName.setId("tf_customer_name");

		TextField customerLineId = new TextField("Customer Line Id");
		customerLineId.setId("tf_customer_line_id");

		HKIDEntryField customerHKID = new HKIDEntryField("Customer HKID");
		customerHKID.setId("tf_customer_hkid");

		PhoneNumberEntryField customerPhone = new PhoneNumberEntryField("Phone Number", "852");
		customerPhone.setId("tf_customer_phone");

		TextField customerAge = new TextField("Customer Age");
		customerAge.setId("tf_customer_age");

		if (customerToSave.getId() == null) { // passed in an unsaved object
			subwindow = new Window("Create new customer");
		} else {
			subwindow = new Window("Edit a customer");
		}

		FormLayout subContent = new FormLayout();

		subwindow.setWidth("800px");

		subwindow.setContent(subContent);
		subwindow.center();
		subwindow.setClosable(false);
		subwindow.setModal(true);
		subwindow.setResizable(false);
		subwindow.setDraggable(false);

		subContent.addComponent(customerName);
		subContent.addComponent(customerLineId);
		subContent.addComponent(customerHKID);
		subContent.addComponent(customerPhone);
		subContent.addComponent(customerAge);

		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(subwindowConfirm);
		buttonActions.addComponent(new Button("Cancel", event -> subwindow.close()));
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

		binder.setBean(customerToSave);

		subwindowConfirm.addClickListener(event -> {
			BinderValidationStatus<Customer> validationStatus = binder.validate();
			log.info(customerHKID.getValue());

			if (validationStatus.isOk()) {
				binder.writeBeanIfValid(customerToSave);

				log.info("About to save customer [{}]", customerName.getValue());

				customerRepo.save(customerToSave);
				this.refreshData();
				subwindow.close();

				log.info("Saved a new/edited customer [{}] successfully", customerName.getValue());

				binder.removeBean();
			} else {
				StringBuilder stringBuilder = new StringBuilder();

				for (BindingValidationStatus<?> result : validationStatus.getFieldValidationErrors()) {
					if (result.getField() instanceof AbstractField && result.getMessage().isPresent()) {
						stringBuilder.append(((AbstractField) result.getField()).getCaption()).append(" ")
								.append(result.getMessage().get()).append("\n");
					}
				}
				Notification.show("Could not create/edit customer!", stringBuilder.toString(),
						Notification.TYPE_ERROR_MESSAGE);
			}
		});

		return subwindow;
	}

	public interface ChangeHandler {
		void onChange();
	}

	public void refreshData() {
		Iterable<Customer> customers = customerRepo.findAll();
		// it's possible the customerRepo can return null!
		if (null == customers) {
			customers = new HashSet<>();
		}
		Collection<Customer> customerCollectionCached = new HashSet<Customer>();
		customerCollectionCached.clear();
		customers.forEach(customerCollectionCached::add);
		ListDataProvider<Customer> provider = new ListDataProvider<Customer>(customerCollectionCached);
		customersGrid.setDataProvider(provider);
	}
}
