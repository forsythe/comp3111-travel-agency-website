package comp3111.input.editors;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import comp3111.Utils;
import comp3111.data.DB;
import comp3111.data.DBManager;
import comp3111.data.model.Booking;
import comp3111.data.model.Customer;
import comp3111.data.model.Offering;
import comp3111.data.repo.BookingRepository;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.OfferingRepository;
import comp3111.input.exceptions.OfferingOutOfRoomException;
import comp3111.input.validators.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class BookingEditor extends VerticalLayout {
	private static final Logger log = LoggerFactory.getLogger(BookingEditor.class);

	private Grid<Booking> bookingGrid = new Grid<>(Booking.class);

	private Booking selectedBookingRecord;

	@Autowired
	private BookingRepository bookingRepo;
	@Autowired
	private CustomerRepository customerRepo;
	@Autowired
	private OfferingRepository offeringRepo;
	@Autowired
	private DBManager actionManager;

	@Autowired
	public BookingEditor(BookingRepository bookingRepo) {
		Button createBookingButton = new Button("Create new booking");
		Button editBookingButton = new Button("Edit booking");

		HorizontalLayout rowOfButtons = new HorizontalLayout();
		rowOfButtons.addComponent(createBookingButton);
		rowOfButtons.addComponent(editBookingButton);

		// disable edit
		editBookingButton.setEnabled(false);

		// add component to view
		this.addComponent(rowOfButtons);

		// get from DB
		Iterable<Booking> bookingRecords = bookingRepo.findAll();
		Collection<Booking> bookingRecordsCached = new HashSet<>();
		bookingRecords.forEach(bookingRecordsCached::add);
		bookingGrid.setItems(bookingRecordsCached);

		bookingGrid.setWidth("100%");
		bookingGrid.setSelectionMode(SelectionMode.SINGLE);

		bookingGrid.addSelectionListener(event -> {
			if (event.getFirstSelectedItem().isPresent()) {
				selectedBookingRecord = event.getFirstSelectedItem().get();
				editBookingButton.setEnabled(true);
				createBookingButton.setEnabled(false);
			} else {
				selectedBookingRecord = null;
				editBookingButton.setEnabled(false);
				createBookingButton.setEnabled(true);
			}
		});

		bookingGrid.removeColumn(DB.BOOKING_NUM_CHILDREN);
		bookingGrid.removeColumn(DB.BOOKING_NUM_ADULTS);
		bookingGrid.removeColumn(DB.BOOKING_NUM_TODDLERS);
		bookingGrid.removeColumn(DB.BOOKING_CUSTOMER);
		bookingGrid.removeColumn(DB.BOOKING_OFFERING);
		bookingGrid.removeColumn(DB.BOOKING_ID);

		bookingGrid.setColumnOrder(DB.BOOKING_CUSTOMER_HKID, DB.BOOKING_CUSTOMER_NAME, DB.BOOKING_OFFERING_ID,
				DB.BOOKING_TOUR_ID, DB.BOOKING_TOUR_NAME, DB.BOOKING_PEOPLE, DB.BOOKING_AMOUNT_PAID,
				DB.BOOKING_TOTAL_COST, DB.BOOKING_SPECIAL_REQUEST, DB.BOOKING_PAYMENT_STATUS);
		bookingGrid.getColumn(DB.BOOKING_PEOPLE).setCaption("Number of Adults, Children, Toddlers");

		for (Column<Booking, ?> col : bookingGrid.getColumns()) {
			col.setMinimumWidth(120);
			col.setHidable(true);
			col.setHidingToggleCaption(col.getCaption());
			col.setExpandRatio(1);
		}
		
		this.addComponent(bookingGrid);

		createBookingButton.addClickListener(event -> {
			getUI().getCurrent().addWindow(getSubwindow(new Booking()));
		});

		editBookingButton.addClickListener(event -> {
			if (canEditBooking(selectedBookingRecord)) {
				getUI().getCurrent().addWindow(getSubwindow(selectedBookingRecord));
			}
		});
	}

	// Check whether a customer offering is editable or not based on start date and
	// current date
	private boolean canEditBooking(Booking booking) {
		if (booking == null)
			return true;

		Date today = Date.from(Instant.now());
		Date threeDayBeforeStart = booking.getOffering().getLastEditableDate();

		if (today.after(threeDayBeforeStart)) {
			Notification.show("It's too late to edit this offering. It cannot be edited after "
					+ Utils.simpleDateFormat(threeDayBeforeStart));
			return false;
		}
		return true;
	}

	private Window getSubwindow(Booking bookingToSave) {
		Window subwindow;

		// Creating the confirm button
		Button confirmButton = new Button("Confirm");
		confirmButton.setId("confirm_booking");

		ComboBox<Customer> customer = new ComboBox<Customer>("Customer");
		ComboBox<Offering> offering = new ComboBox<Offering>("Offering");
		TextField numberAdults = new TextField("Number of adults");
		TextField numberChildren = new TextField("Number of children");
		TextField numberToddlers = new TextField("Number of toddlers");
		TextField amountPaid = new TextField("Amount Paid");
		TextField specialRequest = new TextField("Special Request");
		TextField paymentStatus = new TextField("Payment Status");

		customer.setId("cb_customer");
		offering.setId("cb_offering");
		numberAdults.setId("tf_number_of_adults");
		numberChildren.setId("tf_number_of_children");
		numberToddlers.setId("tf_number_of_toddlers");
		amountPaid.setId("tf_amount_paid");
		specialRequest.setId("tf_special_request");
		paymentStatus.setId("tf_payment_status");

		customer.setPopupWidth(null); // so that the entire text row can be seen
		offering.setPopupWidth(null);

		if (bookingToSave.getId() == null) { // passed in an unsaved object
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

		subContent.addComponent(customer);
		subContent.addComponent(offering);
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

		Collection<Customer> potentialCustomers = new ArrayList<Customer>();

		for (Customer c : customerRepo.findAll()) {
			potentialCustomers.add(c);
		}
		customer.setItems(potentialCustomers);

		Collection<Offering> potentialOfferings = new ArrayList<Offering>();

		for (Offering o : offeringRepo.findAll()) {
			potentialOfferings.add(o);
		}

		offering.setItems(potentialOfferings);

		Binder<Booking> binder = new Binder<Booking>();

		binder.forField(customer).asRequired(Utils.generateRequiredError()).bind(Booking::getCustomer,
				Booking::setCustomer);

		binder.forField(offering).asRequired(Utils.generateRequiredError())
				.withValidator(ValidatorFactory.getOfferingStillOpenValidator())
				.bind(Booking::getOffering, Booking::setOffering);

		binder.forField(numberAdults).asRequired(Utils.generateRequiredError())
				.withConverter(new StringToIntegerConverter("Must be an integer"))
				.withValidator(ValidatorFactory.getIntegerRangeValidator(0))
				.bind(Booking::getNumAdults, Booking::setNumAdults);

		binder.forField(numberChildren).asRequired(Utils.generateRequiredError())
				.withConverter(new StringToIntegerConverter("Must be an integer"))
				.withValidator(ValidatorFactory.getIntegerRangeValidator(0))
				.bind(Booking::getNumChildren, Booking::setNumChildren);

		binder.forField(numberToddlers).asRequired(Utils.generateRequiredError())
				.withConverter(new StringToIntegerConverter("Must be an integer"))
				.withValidator(ValidatorFactory.getIntegerRangeValidator(0))
				.bind(Booking::getNumToddlers, Booking::setNumToddlers);

		binder.forField(amountPaid).asRequired(Utils.generateRequiredError())
				.withConverter(new StringToDoubleConverter("Must be an Number"))
				.withValidator(ValidatorFactory.getDoubleRangeValidator(0))
				.bind(Booking::getAmountPaid, Booking::setAmountPaid);

		binder.forField(specialRequest).withValidator(ValidatorFactory.getStringLengthValidator(255))
				.bind(Booking::getSpecialRequests, Booking::setSpecialRequests);

		binder.forField(paymentStatus).asRequired(Utils.generateRequiredError())
				.withValidator(ValidatorFactory.getStringLengthValidator(255))
				.bind(Booking::getPaymentStatus, Booking::setPaymentStatus);

		binder.setBean(bookingToSave);

		confirmButton.addClickListener(event -> {
			BinderValidationStatus<Booking> validationStatus = binder.validate();

			StringBuilder errorStringBuilder = new StringBuilder();

			if (validationStatus.isOk()) {
				binder.writeBeanIfValid(bookingToSave);

				log.info("About to save booking [{}]", bookingToSave);

				try {
					if (bookingToSave.getId() == null) {
						actionManager.createBookingForOffering(bookingToSave);
						log.info("Saved a new booking [{}] successfully", bookingToSave);

					} else {
						bookingRepo.delete(bookingToSave);
						actionManager.createBookingForOffering(bookingToSave);
						log.info("Saved an edited booking [{}] successfully", bookingToSave);

					}
					binder.removeBean();
					this.refreshData();
					subwindow.close();

					return; // This return skip the error reporting procedure below
				} catch (OfferingOutOfRoomException e) {
					errorStringBuilder.append("Not enough room in offering");
				}

			}

			for (BindingValidationStatus<?> result : validationStatus.getFieldValidationErrors()) {
				if (result.getField() instanceof AbstractField && result.getMessage().isPresent()) {
					errorStringBuilder.append(((AbstractField) result.getField()).getCaption()).append(": ")
							.append(result.getMessage().get()).append("\n");
				}
			}

			Notification.show("Could not create/edit customer!", errorStringBuilder.toString(),
					Notification.TYPE_ERROR_MESSAGE);
		});

		return subwindow;
	}

	public void refreshData() {
		Iterable<Booking> bookings = bookingRepo.findAll();
		// it's possible the bookingRepo can return null!
		if (null == bookings) {
			bookings = new HashSet<>();
		}
		Collection<Booking> customerCollectionCached = new HashSet<>();
		customerCollectionCached.clear();
		bookings.forEach(customerCollectionCached::add);
		ListDataProvider<Booking> provider = new ListDataProvider<>(customerCollectionCached);
		bookingGrid.setDataProvider(provider);
	}

}
