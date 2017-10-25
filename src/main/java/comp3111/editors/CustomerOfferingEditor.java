package comp3111.editors;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.SelectionMode;
import comp3111.converters.CustomerIDConverter;
import comp3111.converters.TourOfferingIDConverter;
import comp3111.exceptions.OfferingOutOfRoomException;
import comp3111.model.ActionManager;
import comp3111.model.CustomerOffering;
import comp3111.model.DB;
import comp3111.repo.CustomerOfferingRepository;
import comp3111.validators.Utils;
import comp3111.validators.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class CustomerOfferingEditor extends VerticalLayout {
	private static final Logger log = LoggerFactory.getLogger(CustomerOfferingEditor.class);

	private Grid<CustomerOffering> customerOfferingGrid = new Grid<>(CustomerOffering.class);

	private CustomerOffering selectedCustomerOffering;

	@Autowired
	private CustomerOfferingRepository customerOfferingRepo;
	@Autowired
	private ActionManager actionManager;
	@Autowired
	private TourOfferingIDConverter tourOfferingIDConverter;
	@Autowired
	private CustomerIDConverter customerIDConverter;

	@SuppressWarnings("unchecked")
	@Autowired
	public CustomerOfferingEditor(CustomerOfferingRepository customerOfferingRepo) {
		Button createBookingButton = new Button("Create new booking");
		Button editBookingButton = new Button("Edit booking");

		HorizontalLayout rowOfButtons = new HorizontalLayout();
		rowOfButtons.addComponent(createBookingButton);
		rowOfButtons.addComponent(editBookingButton);

		// disable edit
		editBookingButton.setEnabled(false);

		//add component to view
		this.addComponent(rowOfButtons);

		//get from DB
		Iterable<CustomerOffering> customerOfferings = customerOfferingRepo.findAll();
		Collection<CustomerOffering> customerOfferingsCached = new HashSet<>();
		customerOfferings.forEach(customerOfferingsCached::add);
		customerOfferingGrid.setItems(customerOfferingsCached);

		customerOfferingGrid.setWidth("100%");
		customerOfferingGrid.setSelectionMode(SelectionMode.SINGLE);

		customerOfferingGrid.addSelectionListener(event -> {
			if (event.getFirstSelectedItem().isPresent()) {
				selectedCustomerOffering = event.getFirstSelectedItem().get();
				editBookingButton.setEnabled(true);
				createBookingButton.setEnabled(false);
			}else{
				selectedCustomerOffering = null;
				editBookingButton.setEnabled(false);
				createBookingButton.setEnabled(true);
			}
		});

		customerOfferingGrid.removeColumn(DB.HIBERNATE_NEW_COL); // hibernate attributes, we don't care about it
		customerOfferingGrid.removeColumn(DB.CUSTOMER_OFFERING_NUM_CHILDREN);
		customerOfferingGrid.removeColumn(DB.CUSTOMER_OFFERING_NUM_ADULTS);
		customerOfferingGrid.removeColumn(DB.CUSTOMER_OFFERING_NUM_TODDLERS);
		customerOfferingGrid.removeColumn(DB.CUSTOMER_OFFERING_CUSTOMER);
		customerOfferingGrid.removeColumn(DB.CUSTOMER_OFFERING_OFFERING);
		customerOfferingGrid.removeColumn(DB.CUSTOMER_OFFERING_ID);

		customerOfferingGrid.setColumnOrder(DB.CUSTOMER_OFFERING_CUSTOMER_HKID, DB.CUSTOMER_OFFERING_CUSTOMER_NAME,
				DB.CUSTOMER_OFFERING_OFFERING_ID, DB.CUSTOMER_OFFERING_TOUR_ID, DB.CUSTOMER_OFFERING_TOUR_NAME,
				DB.CUSTOMER_OFFERING_PEOPLE, DB.CUSTOMER_OFFERING_AMOUNT_PAID, DB.CUSTOMER_OFFERING_TOTAL_COST,
				DB.CUSTOMER_OFFERING_SPECIAL_REQUEST, DB.CUSTOMER_OFFERING_PAYMENT_STATUS);
		customerOfferingGrid.getColumn(DB.CUSTOMER_OFFERING_PEOPLE).setCaption("Number of Adults, Children, Toddlers");

		this.addComponent(customerOfferingGrid);

		createBookingButton.addClickListener(event -> {
			getUI().getCurrent().addWindow(getSubwindow(new CustomerOffering()));
		});

		editBookingButton.addClickListener(event -> {
			if (checkCustomerOfferingEditable(selectedCustomerOffering)) {
				getUI().getCurrent().addWindow(getSubwindow(selectedCustomerOffering));
			}
		});
	}

	//Check whether a customer offering is editable or not based on start date and current date
	private boolean checkCustomerOfferingEditable(CustomerOffering customerOffering){
		if (customerOffering == null) return true;

		Date today = Date.from(Instant.now());
		Date threeDayBeforeStart = customerOffering.getOffering().getLastEditableDate();

		if (today.after(threeDayBeforeStart)) {
			Notification.show("It's too late to edit this offering. It cannot be edited after "
					+ new SimpleDateFormat("dd-MM-yyyy").format(threeDayBeforeStart));
			return false;
		}
		return true;
	}

	private Window getSubwindow(CustomerOffering customerOfferingToSave) {
		Window subwindow;

		//Creating the confirm button
		Button confirmButton = new Button("Confirm");
		confirmButton.setId("confirm_customer_offering");

		TextField customerID = new TextField("Customer ID");

		TextField offeringID = new TextField("Offering ID");
		TextField numberAdults = new TextField("Number of adults");
		TextField numberChildren = new TextField("Number of children");
		TextField numberToddlers = new TextField("Number of toddlers");
		TextField amountPaid = new TextField("Amount Paid");
		TextField specialRequest = new TextField("Special Request");
		TextField paymentStatus = new TextField("Payment Status");

		customerID.setId("tf_customer_id");
		offeringID.setId("tf_offering_id");
		numberAdults.setId("tf_number_of_adults");
		numberChildren.setId("tf_number_of_children");
		numberToddlers.setId("tf_number_of_toddlers");
		amountPaid.setId("tf_amount_paid");
		specialRequest.setId("tf_special_request");
		paymentStatus.setId("tf_payment_status");

		if (customerOfferingToSave.getId() == null) { // passed in an unsaved object
			subwindow = new Window("Create new customer");
		}
		else {
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

		subContent.addComponent(customerID);
		subContent.addComponent(offeringID);
		subContent.addComponent(numberAdults);
		subContent.addComponent(numberChildren);
		subContent.addComponent(numberToddlers);
		subContent.addComponent(amountPaid);
		subContent.addComponent(specialRequest);
		subContent.addComponent(paymentStatus);

		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(confirmButton);
		buttonActions.addComponent(new Button("Cancel", event -> subwindow.close()));
		subContent.addComponent(buttonActions);

		Binder<CustomerOffering> binder = new Binder<>();

		binder.forField(customerID).asRequired(Utils.generateRequiredError())
				.withConverter(new StringToLongConverter("Must be an integer"))
				.withConverter(customerIDConverter)
				.bind(CustomerOffering::getCustomer, CustomerOffering::setCustomer);

		binder.forField(offeringID).asRequired(Utils.generateRequiredError())
				.withConverter(new StringToLongConverter("Must be an integer"))
				.withConverter(tourOfferingIDConverter)
				.withValidator(ValidatorFactory.getOfferingStillOpenValidator())
				.bind(CustomerOffering::getOffering, CustomerOffering::setOffering);

		binder.forField(numberAdults).asRequired(Utils.generateRequiredError())
				.withValidator(ValidatorFactory.getIntegerLowerBoundValidator(0))
				.withConverter(new StringToIntegerConverter("Must be an integer"))
				.bind(CustomerOffering::getNumAdults, CustomerOffering::setNumAdults);

		binder.forField(numberChildren).asRequired(Utils.generateRequiredError())
				.withValidator(ValidatorFactory.getIntegerLowerBoundValidator(0))
				.withConverter(new StringToIntegerConverter("Must be an integer"))
				.bind(CustomerOffering::getNumChildren, CustomerOffering::setNumChildren);

		binder.forField(numberToddlers).asRequired(Utils.generateRequiredError())
				.withValidator(ValidatorFactory.getIntegerLowerBoundValidator(0))
				.withConverter(new StringToIntegerConverter("Must be an integer"))
				.bind(CustomerOffering::getNumToddlers, CustomerOffering::setNumToddlers);

		binder.forField(amountPaid).asRequired(Utils.generateRequiredError())
				.withValidator(ValidatorFactory.getIntegerLowerBoundValidator(0))
				.withConverter(new StringToDoubleConverter("Must be an Number"))
				.bind(CustomerOffering::getAmountPaid, CustomerOffering::setAmountPaid);

		binder.forField(specialRequest).asRequired(Utils.generateRequiredError())
				.withValidator(ValidatorFactory.getStringLengthValidator(255))
				.bind(CustomerOffering::getSpecialRequests, CustomerOffering::setSpecialRequests);

		binder.forField(paymentStatus).asRequired(Utils.generateRequiredError())
				.withValidator(ValidatorFactory.getStringLengthValidator(255))
				.bind(CustomerOffering::getPaymentStatus, CustomerOffering::setPaymentStatus);

		binder.setBean(customerOfferingToSave);

		confirmButton.addClickListener(event -> {
			BinderValidationStatus<CustomerOffering> validationStatus = binder.validate();

			StringBuilder errorStringBuilder = new StringBuilder();

			if (validationStatus.isOk()) {
				binder.writeBeanIfValid(customerOfferingToSave);

				log.info("About to save customer offering [{}]", customerOfferingToSave);

				try {
					actionManager.createBookingForOffering(customerOfferingToSave);
					binder.removeBean();
					this.refreshData();
					subwindow.close();
					log.info("Saved a new/edited customer offering [{}] successfully", customerOfferingToSave);

					return; //This return skip the error reporting procedure below
				}catch (OfferingOutOfRoomException e){
					errorStringBuilder.append("Not enough room in offering");
				}

			}


			for (BindingValidationStatus<?> result : validationStatus.getFieldValidationErrors()) {
				if (result.getField() instanceof AbstractField && result.getMessage().isPresent()) {
					errorStringBuilder.append(((AbstractField) result.getField()).getCaption()).append(" ")
							.append(result.getMessage().get()).append("\n");
				}
			}
			Notification.show("Could not create/edit customer!", errorStringBuilder.toString(),
					Notification.TYPE_ERROR_MESSAGE);
		});

		return subwindow;
	}

	private void refreshData() {
		Iterable<CustomerOffering> customerOfferings = customerOfferingRepo.findAll();
		//it's possible the customerOfferingRepo can return null!
		if (null == customerOfferings) {
			customerOfferings = new HashSet<>();
		}
		Collection<CustomerOffering> customerCollectionCached = new HashSet<>();
		customerCollectionCached.clear();
		customerOfferings.forEach(customerCollectionCached::add);
		ListDataProvider<CustomerOffering> provider = new ListDataProvider<>(customerCollectionCached);
		customerOfferingGrid.setDataProvider(provider);
	}
}
