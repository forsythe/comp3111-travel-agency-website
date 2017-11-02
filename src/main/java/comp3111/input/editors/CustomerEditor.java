package comp3111.input.editors;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;

import comp3111.Utils;
import comp3111.data.GridCol;
import comp3111.data.model.Customer;
import comp3111.data.model.Tour;
import comp3111.data.repo.CustomerRepository;
import comp3111.input.converters.ConverterFactory;
import comp3111.input.field.HKIDEntryField;
import comp3111.input.field.PhoneNumberEntryField;
import comp3111.input.validators.ValidatorFactory;
import comp3111.view.NotificationFactory;

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

	private final HashMap<String, ProviderAndPredicate<?, ?>> gridFilters = new HashMap<String, ProviderAndPredicate<?, ?>>();
	
	@Autowired
	public CustomerEditor(CustomerRepository cr) {
		this.customerRepo = cr;

		// adding components
		HorizontalLayout rowOfButtons = new HorizontalLayout();

		rowOfButtons.addComponent(createNewCustomerButton);
		rowOfButtons.addComponent(editCustomerButton);
		rowOfButtons.addComponent(viewCustomerBookingsButton);
		createNewCustomerButton.setId("btn_create_customer");
		editCustomerButton.setId("btn_edit_customer");
		viewCustomerBookingsButton.setId("btn_view_customer_bookings");

		// Shouldn't be enabled unless selected
		editCustomerButton.setEnabled(false);
		viewCustomerBookingsButton.setEnabled(false);

		// Render component
		this.addComponent(rowOfButtons);

		// Get from GridCol
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

		customersGrid.setColumnOrder(GridCol.CUSTOMER_ID, GridCol.CUSTOMER_NAME, GridCol.CUSTOMER_LINE_ID, GridCol.CUSTOMER_HKID,
				GridCol.CUSTOMER_PHONE, GridCol.CUSTOMER_AGE);

		HeaderRow filterRow = customersGrid.appendHeaderRow();

		for (Column<Customer, ?> col : customersGrid.getColumns()) {
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
				ListDataProvider<Customer> dataProvider = (ListDataProvider<Customer>) customersGrid.getDataProvider();

				if (!filterField.isEmpty()) {
					try {
						// note: if we keep typing into same textfield, we will overwrite the old filter
						// for this column, which is desirable (rather than having filters for "h",
						// "he", "hel", etc
						gridFilters.put(colId, FilterFactory.getFilterForCustomer(colId, searchVal));
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
		subwindowConfirm.setId("btn_confirm_customer");

		TextField customerName = new TextField("Name");
		customerName.setId("tf_customer_name");

		TextField customerLineId = new TextField("Line Id");
		customerLineId.setId("tf_customer_line_id");

		HKIDEntryField customerHKID = new HKIDEntryField("HKID");
		customerHKID.setId("tf_customer_hkid");

		PhoneNumberEntryField customerPhone = new PhoneNumberEntryField("Phone", "852");
		customerPhone.setId("tf_customer_phone");

		TextField customerAge = new TextField("Age");
		customerAge.setId("tf_customer_age");

		if (customerToSave.getId() == null) { // passed in an unsaved object
			subwindow = new Window("Create new customer");
		} else {
			subwindow = new Window("Edit a customer");
		}

		FormLayout form = new FormLayout();

		subwindow.setWidth("800px");

		VerticalLayout formContainer = new VerticalLayout();
		formContainer.addComponent(form);
		subwindow.setContent(formContainer);

		subwindow.center();
		subwindow.setClosable(false);
		subwindow.setModal(true);
		subwindow.setResizable(false);
		subwindow.setDraggable(false);

		form.addComponent(customerName);
		form.addComponent(customerLineId);
		form.addComponent(customerHKID);
		form.addComponent(customerPhone);
		form.addComponent(customerAge);

		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(subwindowConfirm);
		buttonActions.addComponent(new Button("Cancel", event -> subwindow.close()));
		form.addComponent(buttonActions);

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

		binder.forField(customerAge).asRequired(Utils.generateRequiredError())
				.withConverter(ConverterFactory.getStringToIntegerConverter())
				.withValidator(ValidatorFactory.getIntegerRangeValidator(0)).bind(Customer::getAge, Customer::setAge);

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
				NotificationFactory.getTopBarSuccessNotification().show(Page.getCurrent());

				binder.removeBean();
			} else {
				String errors = ValidatorFactory.getValidatorErrorsString(validationStatus);
				NotificationFactory.getTopBarWarningNotification(errors, 5).show(Page.getCurrent());
			}
		});

		return subwindow;
	}

	public interface ChangeHandler {
		void onChange();
	}

	public void refreshData() {

		ListDataProvider<Customer> provider = new ListDataProvider<Customer>(
				Utils.iterableToCollection(customerRepo.findAll()));
		customersGrid.setDataProvider(provider);

	}
}
