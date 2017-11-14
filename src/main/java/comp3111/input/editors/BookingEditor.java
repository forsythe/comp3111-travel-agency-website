package comp3111.input.editors;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import comp3111.Utils;
import comp3111.data.GridCol;
import comp3111.data.DBManager;
import comp3111.data.model.Booking;
import comp3111.data.model.Customer;
import comp3111.data.model.Offering;
import comp3111.data.model.Tour;
import comp3111.data.repo.BookingRepository;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.OfferingRepository;
import comp3111.input.converters.ConverterFactory;
import comp3111.input.exceptions.OfferingOutOfRoomException;
import comp3111.input.validators.ValidatorFactory;
import comp3111.view.NotificationFactory;

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

	private final HashMap<String, ProviderAndPredicate<?, ?>> gridFilters = new HashMap<String, ProviderAndPredicate<?, ?>>();

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

		// get from GridCol
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

		bookingGrid.removeColumn(GridCol.BOOKING_NUM_CHILDREN);
		bookingGrid.removeColumn(GridCol.BOOKING_NUM_ADULTS);
		bookingGrid.removeColumn(GridCol.BOOKING_NUM_TODDLERS);
		bookingGrid.removeColumn(GridCol.BOOKING_CUSTOMER);
		bookingGrid.removeColumn(GridCol.BOOKING_OFFERING);
		bookingGrid.removeColumn(GridCol.BOOKING_ID);

		bookingGrid.setColumnOrder(GridCol.BOOKING_CUSTOMER_HKID, GridCol.BOOKING_CUSTOMER_NAME,
				GridCol.BOOKING_OFFERING_ID, GridCol.BOOKING_TOUR_ID, GridCol.BOOKING_TOUR_NAME, GridCol.BOOKING_PEOPLE,
				GridCol.BOOKING_AMOUNT_PAID, GridCol.BOOKING_TOTAL_COST, GridCol.BOOKING_SPECIAL_REQUEST,
				GridCol.BOOKING_PAYMENT_STATUS);
		bookingGrid.getColumn(GridCol.BOOKING_PEOPLE).setCaption("Number of Adults, Children, Toddlers");

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
			NotificationFactory
					.getTopBarWarningNotification("It's too late to edit this offering. It can't be editied after "
							+ Utils.simpleDateFormat(threeDayBeforeStart), 5)
					.show(Page.getCurrent());

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
		ComboBox<String> paymentStatus = new ComboBox<>("Payment Status");

		customer.setId("cb_customer");
		offering.setId("cb_offering");
		numberAdults.setId("tf_number_of_adults");
		numberChildren.setId("tf_number_of_children");
		numberToddlers.setId("tf_number_of_toddlers");
		amountPaid.setId("tf_amount_paid");
		specialRequest.setId("tf_special_request");
		paymentStatus.setId("cb_payment_status");

		customer.setPopupWidth(null); // so that the entire text row can be seen
		offering.setPopupWidth(null);
		paymentStatus.setPopupWidth(null);

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

		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(confirmButton);
		buttonActions.addComponent(new Button("Cancel", event -> subwindow.close()));
		form.addComponent(buttonActions);

		customer.setItems(Utils.iterableToCollection(customerRepo.findAll()).stream()
				.sorted((c1, c2) -> c1.getId().compareTo(c2.getId())));

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

		binder.setBean(bookingToSave);

		confirmButton.addClickListener(event -> {
			BinderValidationStatus<Booking> validationStatus = binder.validate();

			String errors = ValidatorFactory.getValidatorErrorsString(validationStatus);

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
					NotificationFactory.getTopBarSuccessNotification().show(Page.getCurrent());

					return; // This return skip the error reporting procedure below
				} catch (OfferingOutOfRoomException e) {
					errors += "Not enough room in offering";
				}

			}
			NotificationFactory.getTopBarWarningNotification(errors, 5).show(Page.getCurrent());

		});

		return subwindow;
	}

	public void refreshData() {

		ListDataProvider<Booking> provider = new ListDataProvider<>(Utils.iterableToCollection(bookingRepo.findAll()));
		bookingGrid.setDataProvider(provider);
	}

}
