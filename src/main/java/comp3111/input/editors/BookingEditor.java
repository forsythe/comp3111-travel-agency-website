package comp3111.input.editors;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import comp3111.data.model.PromoEvent;
import comp3111.data.repo.PromoEventRepository;
import comp3111.input.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;

import comp3111.Utils;
import comp3111.data.DBManager;
import comp3111.data.GridCol;
import comp3111.data.model.Booking;
import comp3111.data.model.Customer;
import comp3111.data.model.Offering;
import comp3111.data.repo.BookingRepository;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.OfferingRepository;
import comp3111.input.converters.ConverterFactory;
import comp3111.input.validators.ValidatorFactory;
import comp3111.view.NotificationFactory;

/**
 * Represents the booking editor in the Booking management view
 * 
 * @author Forsythe
 *
 */
@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class BookingEditor extends VerticalLayout {
	private static final Logger log = LoggerFactory.getLogger(BookingEditor.class);

	private Grid<Booking> bookingGrid = new Grid<>(Booking.class);

	private Booking selectedBookingRecord;

	private BookingRepository bookingRepo;
	private CustomerRepository customerRepo;
	private OfferingRepository offeringRepo;
	private PromoEventRepository promoRepo;
	private DBManager dbManager;

	// the fields in the subwindow
	private ComboBox<Customer> customer;
	private ComboBox<Offering> offering;
	private TextField numberAdults;
	private TextField numberChildren;
	private TextField numberToddlers;
	private TextField amountPaid;
	private TextField specialRequest;
	private ComboBox<String> paymentStatus;
	private ComboBox<String> promoCode;
	private Button confirmButton;
	private BinderValidationStatus<Booking> validationStatus;

	private final HashMap<String, ProviderAndPredicate<?, ?>> gridFilters = new HashMap<String, ProviderAndPredicate<?, ?>>();

	/**
	 * Creates a new Booking editor. All fields autowired.
	 * 
	 */
	@Autowired
	public BookingEditor(BookingRepository br, CustomerRepository cr, OfferingRepository or, PromoEventRepository per,
			DBManager dbm) {
		this.bookingRepo = br;
		this.customerRepo = cr;
		this.offeringRepo = or;
		this.promoRepo = per;
		this.dbManager = dbm;

		Button createBookingButton = new Button("Create new booking");
		Button editBookingButton = new Button("Edit booking");

		HorizontalLayout rowOfButtons = new HorizontalLayout();
		rowOfButtons.addComponent(createBookingButton);
		rowOfButtons.addComponent(editBookingButton);

		// disable edit
		editBookingButton.setEnabled(false);

		// add component to view
		this.addComponent(rowOfButtons);

		// get from GridCol
		if (bookingRepo.findAll() != null) // can be null if mockito
			bookingGrid.setItems(Utils.iterableToCollection(bookingRepo.findAll()).stream()
					.sorted((b1, b2) -> b1.getId().compareTo(b2.getId())));

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

		bookingGrid.removeColumn(GridCol.BOOKING_CUSTOMER);
		bookingGrid.removeColumn(GridCol.BOOKING_ID);
		bookingGrid.removeColumn(GridCol.BOOKING_PROMO_DISCOUNT_MULTIPLIER);
		bookingGrid.removeColumn(GridCol.BOOKING_TOTAL_NUMBER_OF_PEOPLE);
		bookingGrid.removeColumn(GridCol.BOOKING_PROMO_CODE_USED);

		bookingGrid.setColumnOrder(GridCol.BOOKING_CUSTOMER_HKID, GridCol.BOOKING_CUSTOMER_NAME,
				GridCol.BOOKING_OFFERING, GridCol.BOOKING_NUM_ADULTS, GridCol.BOOKING_NUM_CHILDREN,
				GridCol.BOOKING_NUM_TODDLERS, GridCol.BOOKING_TOUR_ID, GridCol.BOOKING_TOUR_NAME,
				GridCol.BOOKING_AMOUNT_PAID, GridCol.BOOKING_TOTAL_COST, GridCol.BOOKING_SPECIAL_REQUEST,
				GridCol.BOOKING_PAYMENT_STATUS);

		bookingGrid.addColumn(b -> {
			if (b.getPromoDiscountMultiplier() != 1) {
				return String.valueOf(b.getPromoDiscountMultiplier());
			} else {
				return "none";
			}
		}).setId(GridCol.BOOKING_PROMO_DISCOUNT_MULTIPLIER_MASKED).setCaption("Promotional Discount");

		HeaderRow filterRow = bookingGrid.appendHeaderRow();

		for (Column<Booking, ?> col : bookingGrid.getColumns()) {
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
				ListDataProvider<Booking> dataProvider = (ListDataProvider<Booking>) bookingGrid.getDataProvider();

				if (!filterField.isEmpty()) {
					try {
						gridFilters.put(colId, FilterFactory.getFilterForBooking(colId, searchVal));
						log.info("updated filter on attribute [{}]", colId);

					} catch (Exception e) {
						log.info("ignoring val=[{}], col=[{}] is invalid", searchVal, colId);
					}
				} else {
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

		this.addComponent(bookingGrid);

		createBookingButton.addClickListener(event -> {
			getUI();
			UI.getCurrent().addWindow(getSubwindow(new Booking()));
		});

		editBookingButton.addClickListener(event -> {
			if (canEditBooking(selectedBookingRecord)) {
				getUI();
				UI.getCurrent().addWindow(getSubwindow(selectedBookingRecord));
			}
		});
	}

	/**
	 * Check whether a customer offering is editable or not based on start date and
	 * current date
	 * 
	 * @param booking
	 *            The booking to check
	 * @return Whether the booking is editable
	 */
	public boolean canEditBooking(Booking booking) {
		if (booking == null)
			return true;

		Date today = Date.from(Instant.now());
		Date threeDayBeforeStart = booking.getOffering().getLastEditableDate();

		if (today.after(threeDayBeforeStart)) {
			if (Page.getCurrent() != null) // can be null if using mockito
				NotificationFactory
						.getTopBarWarningNotification("It's too late to edit this offering. It can't be editied after "
								+ Utils.simpleDateFormat(threeDayBeforeStart), 5)
						.show(Page.getCurrent());

			return false;
		}
		return true;
	}

	/**
	 * Creates the popup window for creating/editing bookings
	 * 
	 * @param bookingToSave
	 *            The transient or detached booking record to save
	 * @return The window
	 */
	public Window getSubwindow(Booking bookingToSave) {
		Window subwindow;

		// Creating the confirm button
		confirmButton = new Button("Confirm");
		confirmButton.setId("confirm_booking");

		customer = new ComboBox<Customer>("Customer");
		offering = new ComboBox<Offering>("Offering");
		numberAdults = new TextField("Number of adults");
		numberChildren = new TextField("Number of children");
		numberToddlers = new TextField("Number of toddlers");
		amountPaid = new TextField("Amount Paid");
		specialRequest = new TextField("Special Request");
		paymentStatus = new ComboBox<>("Payment Status");
		promoCode = new ComboBox<>("Promotion Code");

		customer.setId("cb_customer");
		offering.setId("cb_offering");
		numberAdults.setId("tf_number_of_adults");
		numberChildren.setId("tf_number_of_children");
		numberToddlers.setId("tf_number_of_toddlers");
		amountPaid.setId("tf_amount_paid");
		specialRequest.setId("tf_special_request");
		paymentStatus.setId("cb_payment_status");
		promoCode.setId("cb_promotion_code");

		customer.setPopupWidth(null); // so that the entire text row can be seen
		offering.setPopupWidth(null);
		paymentStatus.setPopupWidth(null);
		promoCode.setPopupWidth(null);

		if (bookingToSave.getId() == null) { // passed in an unsaved object
			subwindow = new Window("Create new boooking");
		} else {
			subwindow = new Window("Edit a booking");
		}

		FormLayout form = new FormLayout();
		VerticalLayout formContainer = new VerticalLayout();
		formContainer.addComponent(form);

		subwindow.setWidth("800px");
		subwindow.setContent(formContainer);
		subwindow.center();
		subwindow.setClosable(false);
		subwindow.setModal(true);
		subwindow.setResizable(false);
		subwindow.setDraggable(false);

		form.addComponent(customer);
		form.addComponent(offering);
		form.addComponent(numberAdults);
		form.addComponent(numberChildren);
		form.addComponent(numberToddlers);
		form.addComponent(amountPaid);
		form.addComponent(specialRequest);
		form.addComponent(paymentStatus);
		form.addComponent(promoCode);

		offering.addValueChangeListener(event -> {
			Offering o = offering.getValue();
			if (o != null) {
				promoCode.setItems(Utils.iterableToCollection(promoRepo.findByOffering(o)).stream()
						.sorted((c1, c2) -> c1.getId().compareTo(c2.getId())).map(PromoEvent::getPromoCode));
				promoCode.setEnabled(true);
			} else {
				promoCode.setEnabled(false);
			}
		});
		promoCode.setEnabled(false);

		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(confirmButton);
		buttonActions.addComponent(new Button("Cancel", event -> subwindow.close()));
		form.addComponent(buttonActions);

		if (customerRepo.findAll() != null) // can be null if mockito
			customer.setItems(Utils.iterableToCollection(customerRepo.findAll()).stream()
					.sorted((c1, c2) -> c1.getId().compareTo(c2.getId())));

		if (offeringRepo.findAll() != null) // mockito
			offering.setItems(Utils.iterableToCollection(offeringRepo.findAll()).stream()
					.sorted((o1, o2) -> o1.getId().compareTo(o2.getId())));

		Collection<String> potentialPaymentStatus = new ArrayList<>(
				Arrays.asList(Booking.PAYMENT_PENDING, Booking.PAYMENT_CONFIRMED));
		paymentStatus.setItems(potentialPaymentStatus);
		paymentStatus.setSelectedItem(Booking.PAYMENT_PENDING);

		Binder<Booking> binder = new Binder<Booking>();

		binder.forField(customer).asRequired(Utils.generateRequiredError()).bind(Booking::getCustomer,
				Booking::setCustomer);

		binder.forField(offering).asRequired(Utils.generateRequiredError())
				.withValidator(ValidatorFactory.getOfferingStillOpenValidator())
				.bind(Booking::getOffering, Booking::setOffering);

		binder.forField(numberAdults).asRequired(Utils.generateRequiredError())
				.withConverter(ConverterFactory.getStringToIntegerConverter())
				.withValidator(ValidatorFactory.getIntegerRangeValidator(0))
				.bind(Booking::getNumAdults, Booking::setNumAdults);

		binder.forField(numberChildren).asRequired(Utils.generateRequiredError())
				.withConverter(ConverterFactory.getStringToIntegerConverter())
				.withValidator(ValidatorFactory.getIntegerRangeValidator(0))
				.bind(Booking::getNumChildren, Booking::setNumChildren);

		binder.forField(numberToddlers).asRequired(Utils.generateRequiredError())
				.withConverter(ConverterFactory.getStringToIntegerConverter())
				.withValidator(ValidatorFactory.getIntegerRangeValidator(0))
				.bind(Booking::getNumToddlers, Booking::setNumToddlers);

		binder.forField(amountPaid).asRequired(Utils.generateRequiredError())
				.withConverter(ConverterFactory.getStringToDoubleConverter())
				.withValidator(ValidatorFactory.getDoubleRangeValidator(0))
				.bind(Booking::getAmountPaid, Booking::setAmountPaid);

		binder.forField(specialRequest).withValidator(ValidatorFactory.getStringLengthValidator(255))
				.bind(Booking::getSpecialRequests, Booking::setSpecialRequests);

		binder.forField(paymentStatus).asRequired(Utils.generateRequiredError())
				.withValidator(ValidatorFactory.getStringLengthValidator(255))
				.bind(Booking::getPaymentStatus, Booking::setPaymentStatus);

		binder.forField(promoCode).withValidator(ValidatorFactory.getStringLengthCanNullValidator(255))
				.bind(Booking::getPromoCodeUsed, Booking::setPromoCodeUsed);

		binder.setBean(bookingToSave);

		confirmButton.addClickListener(event -> {
			validationStatus = binder.validate();

			String errors = ValidatorFactory.getValidatorErrorsString(validationStatus);

			if (validationStatus.isOk()) {
				binder.writeBeanIfValid(bookingToSave);

				log.info("About to save booking [{}]", bookingToSave);

				try {
					if (promoCode.getValue() != null && !promoCode.isEmpty()) {
						// With promo code
						dbManager.createBookingForOfferingWithPromoCode(bookingToSave, promoCode.getValue());
						log.info("Saved a new booking [{}] with promo code [{}] successfully", bookingToSave,
								promoCode.getValue());
					} else {
						// Without promo code
						dbManager.createNormalBookingForOffering(bookingToSave);
						log.info("Saved a new booking [{}] successfully", bookingToSave);
					}
					if (bookingToSave.getId() == null) {
						bookingRepo.delete(bookingToSave);
					}
					binder.removeBean();
					this.refreshData();
					subwindow.close();
					if (Page.getCurrent() != null) // can be null if using mockito
						NotificationFactory.getTopBarSuccessNotification().show(Page.getCurrent());
				} catch (PromoForCustomerExceededException e) {
					errors += "The customer has exceeded the maximum number of reserations allowed.\n";
				} catch (PromoCodeUsedUpException e) {
					errors += "The promo code has been used up.\n";
				} catch (NoSuchPromoCodeException e) {
					errors += "The promo code does not exist.\n";
				} catch (OfferingOutOfRoomException e) {
					errors += "Not enough room in offering";
				} catch (PromoCodeNotForOfferingException e) {
					errors += "The promo code is not for this offering.\n";
				}
			}

			if (!errors.isEmpty() && Page.getCurrent() != null) {
				NotificationFactory.getTopBarWarningNotification(errors, 5).show(Page.getCurrent());
			}
		});

		return subwindow;
	}

	/**
	 * Refreshes the data in the vaadin grid
	 */
	public void refreshData() {
		if (bookingRepo.findAll() != null) { // mockito
			ListDataProvider<Booking> provider = new ListDataProvider<>(
					Utils.iterableToCollection(bookingRepo.findAll()));
			bookingGrid.setDataProvider(provider);
		}
	}

	/**
	 * @return the customer field
	 */
	public ComboBox<Customer> getCustomer() {
		return customer;
	}

	/**
	 * @return the offering field
	 */
	public ComboBox<Offering> getOffering() {
		return offering;
	}

	/**
	 * @return the numberAdults field
	 */
	public TextField getNumberAdults() {
		return numberAdults;
	}

	/**
	 * @return the numberChildren field
	 */
	public TextField getNumberChildren() {
		return numberChildren;
	}

	/**
	 * @return the numberToddlers field
	 */
	public TextField getNumberToddlers() {
		return numberToddlers;
	}

	/**
	 * @return the amountPaid field
	 */
	public TextField getAmountPaid() {
		return amountPaid;
	}

	/**
	 * @return the specialRequest field
	 */
	public TextField getSpecialRequest() {
		return specialRequest;
	}

	/**
	 * @return the paymentStatus field
	 */
	public ComboBox<String> getPaymentStatus() {
		return paymentStatus;
	}

	/**
	 * @return the promoCode field
	 */
	public ComboBox<String> getPromoCode() {
		return promoCode;
	}

	/**
	 * @return the confirmButton
	 */
	public Button getConfirmButton() {
		return confirmButton;
	}

	/**
	 * @return the validationStatus object
	 */
	public BinderValidationStatus<Booking> getValidationStatus() {
		return validationStatus;
	}

}
