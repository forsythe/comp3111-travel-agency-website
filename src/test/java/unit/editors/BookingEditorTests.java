package unit.editors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.ValidationResult;

import comp3111.Utils;
import comp3111.data.DBManager;
import comp3111.data.model.Booking;
import comp3111.data.model.Customer;
import comp3111.data.model.Offering;
import comp3111.data.model.PromoEvent;
import comp3111.data.model.Tour;
import comp3111.data.model.TourGuide;
import comp3111.data.repo.BookingRepository;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.OfferingRepository;
import comp3111.data.repo.PromoEventRepository;
import comp3111.input.editors.BookingEditor;
import comp3111.input.exceptions.OfferingDateUnsupportedException;
import comp3111.input.exceptions.PromoForCustomerExceededException;
import comp3111.input.exceptions.TourGuideUnavailableException;

/**
 * Uncomment the RunWith and SpringBootTest to actually use autowired stuff.
 * Otherwise, this is way faster.
 */
// @RunWith(SpringRunner.class)
// @SpringBootTest(classes = Application.class)
public class BookingEditorTests {

	// @Autowired
	// private TourRepository tourRepo;
	// @Autowired
	// private DBManager dbManager;

	@InjectMocks
	protected BookingEditor bookingEditor;

	/*
	 * Even though we don't use these objects here, mockito needs them to fill in
	 * the autowired fields in BookingEditor! Don't literally use them.
	 */
	@Mock
	private BookingRepository brMock;
	@Mock
	private CustomerRepository crMock;
	@Mock
	private OfferingRepository orMock;
	@Mock
	private PromoEventRepository prMock;
	@Mock
	private DBManager dbmMock;

	private static final String OVERLY_LONG_STRING = "lllllllllllllllllllllllllllllllll"
			+ "llllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll"
			+ "llllllllllllllllllllllllllllllllllllllllllllllllllllllllllll"
			+ "lllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll"
			+ "llllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll"
			+ "lllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll";
	private Date now;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		now = new Date();
	}

	@After
	public void tearDown() {
		// dbManager.deleteAll();
	}

	@Test
	public void testCanSaveValidBooking() throws OfferingDateUnsupportedException, TourGuideUnavailableException {
		Booking newBooking = new Booking();
		newBooking.setId(123);
		bookingEditor.getSubwindow(newBooking);

		Tour shimenTour = new Tour("Shimen Forest", "Color ponds...", 2, 0.8, 0, 499, 599);
		TourGuide tg1 = new TourGuide();
		shimenTour.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));

		Offering shimenOffering = new Offering(shimenTour, tg1, Utils.addDate(now, 100), "Hotel chep", 4, 20,
				Offering.STATUS_PENDING);

		shimenOffering.setStatus(Offering.STATUS_PENDING);
		shimenOffering.setStartDate(Utils.addDate(now, 10));

		Customer c = new Customer();

		setEditorFormValues(c, shimenOffering, "2", "1", "1", "0", "non smoking room", Booking.PAYMENT_PENDING, null);

		bookingEditor.getConfirmButton().click();

		assertTrue(bookingEditor.getValidationStatus().isOk());
		// assertEquals(7,
		// tourEditor.getValidationStatus().getFieldValidationErrors().size());
	}

	@Test
	public void testAllInvalidInputsCaughtWhenMakingBooking() {
		bookingEditor.getSubwindow(new Booking());

		Tour shimenTour = new Tour("Shimen Forest", "Color ponds...", 2, 0.8, 0, 499, 599);
		TourGuide tg1 = new TourGuide();
		shimenTour.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));

		Offering shimenOffering = new Offering(shimenTour, tg1, Utils.addDate(now, 100), "Hotel chep", 4, 20,
				Offering.STATUS_PENDING);

		shimenOffering.setStatus(Offering.STATUS_PENDING);
		shimenOffering.setStartDate(Utils.addDate(now, 10));

		Customer c = new Customer();

		setEditorFormValues(null, null, "-2", "-1", "-1", "-10", "non smoking room", Booking.PAYMENT_PENDING, null);

		bookingEditor.getConfirmButton().click();

		assertFalse(bookingEditor.getValidationStatus().isOk());
		assertEquals(6, bookingEditor.getValidationStatus().getFieldValidationErrors().size());
	}

	@Test
	public void testCanEditBooking() {
		assertTrue(bookingEditor.canEditBooking(null));

		Booking tooLate = new Booking();
		Offering tooLateOffering = new Offering();
		tooLateOffering.setStartDate(Utils.addDate(new Date(), -10));
		tooLate.setOffering(tooLateOffering);
		assertFalse(bookingEditor.canEditBooking(tooLate));

		Booking editable = new Booking();
		Offering futureOffering = new Offering();
		futureOffering.setStartDate(Utils.addDate(new Date(), 10));
		editable.setOffering(futureOffering);
		assertTrue(bookingEditor.canEditBooking(editable));
	}

	private void setEditorFormValues(Customer c, Offering o, String numberAdults, String numberChilden,
			String numberToddlers, String amountPaid, String specialRequest, String paymentStatus, String promoCode) {
		bookingEditor.getCustomer().setValue(c);
		bookingEditor.getOffering().setValue(o);
		bookingEditor.getNumberAdults().setValue(numberAdults);
		bookingEditor.getNumberChildren().setValue(numberChilden);
		bookingEditor.getNumberToddlers().setValue(numberToddlers);
		bookingEditor.getAmountPaid().setValue(amountPaid);
		bookingEditor.getSpecialRequest().setValue(specialRequest);
		bookingEditor.getPaymentStatus().setValue(paymentStatus);
		bookingEditor.getPromoCode().setValue(promoCode);

	}

}