package unit;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import comp3111.Application;
import comp3111.Utils;
import comp3111.data.DBManager;
import comp3111.data.model.Booking;
import comp3111.data.model.Customer;
import comp3111.data.model.LoginUser;
import comp3111.data.model.Offering;
import comp3111.data.model.PromoEvent;
import comp3111.data.model.Tour;
import comp3111.data.model.TourGuide;
import comp3111.data.repo.BookingRepository;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.LoginUserRepository;
import comp3111.data.repo.NonFAQQueryRepository;
import comp3111.data.repo.OfferingRepository;
import comp3111.data.repo.PromoEventRepository;
import comp3111.data.repo.TourGuideRepository;
import comp3111.data.repo.TourRepository;
import comp3111.input.auth.Authentication;
import comp3111.input.exceptions.NoSuchPromoCodeException;
import comp3111.input.exceptions.OfferingDateUnsupportedException;
import comp3111.input.exceptions.OfferingOutOfRoomException;
import comp3111.input.exceptions.PromoCodeNotForOfferingException;
import comp3111.input.exceptions.PromoCodeUsedUpException;
import comp3111.input.exceptions.PromoForCustomerExceededException;
import comp3111.input.exceptions.TourGuideUnavailableException;
import comp3111.input.exceptions.UsernameTakenException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Ignore("all works, but temporarily pausing because my phone cant handle")
public class DBManagerTests {

	@Autowired
	private CustomerRepository customerRepo;
	@Autowired
	private BookingRepository bookingRepo;
	@Autowired
	private LoginUserRepository loginUserRepo;
	@Autowired
	private OfferingRepository offeringRepo;
	@Autowired
	private TourRepository tourRepo;
	@Autowired
	private TourGuideRepository tourGuideRepo;
	@Autowired
	private NonFAQQueryRepository nonFAQQuery;
	@Autowired
	private PromoEventRepository promoRepo;
	@Autowired
	private Authentication authentication;
	@Autowired
	private DBManager dbManager;

	Customer c1, c2;
	TourGuide tg1, tg2;
	Date now;

	static final String C1_NAME = "Johnny";
	static final String C2_NAME = "Bobby";
	static final String TG1_NAME = "Amber";
	static final String TG1_LINE_USERNAME = "line123";
	static final String TG2_NAME = "Peppa";
	static final String TG2_LINE_USERNAME = "line445";

	// @Test
	// public void testLol() {
	// Customer heng = customerRepo.findOneByName("Heng");
	// NonFAQQuery f = new NonFAQQuery("how is spaghet", heng);
	// nonFAQQuery.save(f);
	// }

	@Before // called before each test
	public void setUp() {
		dbManager.deleteAll();

		c1 = new Customer(C1_NAME, 30);
		c2 = new Customer(C2_NAME, 30);
		customerRepo.save(c1);
		customerRepo.save(c2);

		tg1 = new TourGuide(TG1_NAME, TG1_LINE_USERNAME);
		tg2 = new TourGuide(TG2_NAME, TG2_LINE_USERNAME);
		tourGuideRepo.save(tg1);
		tourGuideRepo.save(tg2);
		now = new Date();
	}

	@After
	public void tearDown() {
		dbManager.deleteAll();
	}

	@Test
	public void testSuccessDefaultDataSaved() {
		then(this.customerRepo.findById(c1.getId()).size()).isEqualTo(1);
		then(this.customerRepo.findById(c2.getId()).size()).isEqualTo(1);

		then(this.tourGuideRepo.findByName(TG1_NAME).size()).isEqualTo(1);
		then(this.tourGuideRepo.findByName(TG2_NAME).size()).isEqualTo(1);
	}

	@Test
	public void testSuccessShouldHaveAtLeastOneLoginUser() {
		then(this.loginUserRepo.count()).isGreaterThanOrEqualTo(1);
	}

	@Test
	public void testSuccessCreateOfferingAndBookingForUnsavedEntities()
			throws OfferingOutOfRoomException, OfferingDateUnsupportedException, TourGuideUnavailableException {
		TourGuide anonTg = new TourGuide("lucy", "LINEID123");
		Customer anonCust = new Customer("morgan freeman", 35);
		Tour anonTour = new Tour("Shimen Forest", "Color ponds...", 2, 0.8, 0, 499, 599);
		anonTour.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));

		// testing with unsaved offering object (peppaPig, shimenTour)
		Offering anonOffering = dbManager.createOfferingForTour(anonTour, anonTg, Utils.addDate(now, 10), "Hotel chep",
				4, 20);
		Booking anonBooking = dbManager.createBookingForOffering(anonOffering, anonCust, 5, 2, 3, 0, "no smoking",
				Booking.PAYMENT_CONFIRMED, 1);

		then(this.tourGuideRepo.findByName(anonTg.getName()).size()).isEqualTo(1);
		then(this.customerRepo.findByName(anonCust.getName()).size()).isEqualTo(1);
		then(this.tourRepo.findByTourName(anonTour.getTourName()).size()).isEqualTo(1);

		then(this.offeringRepo.findByHotelName(anonOffering.getHotelName()).size()).isEqualTo(1);
		then(this.bookingRepo.findByPaymentStatus(anonBooking.getPaymentStatus()).size()).isEqualTo(1);
	}

	@Test
	public void testSuccessCreateOfferingAndBookingForSavedEntities()
			throws OfferingOutOfRoomException, OfferingDateUnsupportedException, TourGuideUnavailableException {

		Tour tour1 = new Tour("Yangshan", "Many hotsprings", 3, 0.8, 0.0, 599, 699);
		tour1.setAllowedDates(
				new HashSet<Date>(Arrays.asList(new GregorianCalendar(2017, Calendar.DECEMBER, 9).getTime(),
						new GregorianCalendar(2017, Calendar.DECEMBER, 12).getTime())));

		tourRepo.save(tour1);

		Offering yangShanOffering = dbManager.createOfferingForTour(tourRepo.findOne(tour1.getId()),
				tourGuideRepo.findOne(tg1.getId()), new ArrayList<Date>(tour1.getAllowedDates()).get(0), "hotel bob", 5,
				18);
		Booking booking = dbManager.createBookingForOffering(offeringRepo.findOne(yangShanOffering.getId()),
				customerRepo.findOne(c1.getId()), 3, 4, 2, 0, "kids meal", Booking.PAYMENT_PENDING, 1);

		then(this.offeringRepo.findByHotelName(yangShanOffering.getHotelName()).size()).isEqualTo(1);
		then(this.bookingRepo.findByPaymentStatus(booking.getPaymentStatus()).size()).isEqualTo(1);
	}

	@Test
	public void testSuccessTwoCustomersMakeBookingForATour()
			throws OfferingDateUnsupportedException, TourGuideUnavailableException, OfferingOutOfRoomException {
		Tour tour1 = new Tour("Yangshan", "Many hotsprings", 3, 0.8, 0.0, 599, 699);
		tour1.setAllowedDates(
				new HashSet<Date>(Arrays.asList(new GregorianCalendar(2017, Calendar.DECEMBER, 9).getTime(),
						new GregorianCalendar(2017, Calendar.DECEMBER, 12).getTime())));

		tourRepo.save(tour1);

		Offering yangShanOffering = dbManager.createOfferingForTour(tourRepo.findOne(tour1.getId()),
				tourGuideRepo.findOne(tg1.getId()), new ArrayList<Date>(tour1.getAllowedDates()).get(0), "hotel bob", 5,
				18);

		dbManager.createBookingForOffering(offeringRepo.findOne(yangShanOffering.getId()),
				customerRepo.findOne(c1.getId()), 3, 4, 2, 0, "kids meal", Booking.PAYMENT_PENDING, 1);

		dbManager.createBookingForOffering(offeringRepo.findOne(yangShanOffering.getId()),
				customerRepo.findOne(c2.getId()), 3, 4, 2, 0, "no smoking meal", Booking.PAYMENT_PENDING, 1);

		then(this.offeringRepo.findByHotelName(yangShanOffering.getHotelName()).size()).isEqualTo(1);
		then(this.bookingRepo.findByOffering(yangShanOffering).size()).isEqualTo(2);
	}

	@Test
	public void testSuccessCreateTwoOfferingsWithDifferentTourGuidesForATour()
			throws OfferingDateUnsupportedException, TourGuideUnavailableException, OfferingOutOfRoomException {

		Tour tour1 = new Tour("Yangshan", "Many hotsprings", 3, 0.8, 0.0, 599, 699);
		tour1.setAllowedDates(
				new HashSet<Date>(Arrays.asList(new GregorianCalendar(2017, Calendar.DECEMBER, 9).getTime(),
						new GregorianCalendar(2017, Calendar.DECEMBER, 12).getTime())));

		tourRepo.save(tour1);

		Offering offering1 = dbManager.createOfferingForTour(tourRepo.findOne(tour1.getId()),
				tourGuideRepo.findOne(tg1.getId()), new ArrayList<Date>(tour1.getAllowedDates()).get(0), "hotel bob", 5,
				18);
		Booking book1 = dbManager.createBookingForOffering(offeringRepo.findOne(offering1.getId()),
				customerRepo.findOne(c1.getId()), 3, 4, 2, 0, "kids meal", Booking.PAYMENT_PENDING, 1);

		Offering offering2 = dbManager.createOfferingForTour(tourRepo.findOne(tour1.getId()),
				tourGuideRepo.findOne(tg2.getId()), new ArrayList<Date>(tour1.getAllowedDates()).get(0), "hotel cheap",
				5, 18);
		Booking book2 = dbManager.createBookingForOffering(offeringRepo.findOne(offering2.getId()),
				customerRepo.findOne(c2.getId()), 3, 4, 2, 0, "kids meal", Booking.PAYMENT_CONFIRMED, 1);

		then(this.offeringRepo.findByHotelName(offering1.getHotelName()).size()).isEqualTo(1);
		then(this.offeringRepo.findByHotelName(offering2.getHotelName()).size()).isEqualTo(1);
		then(this.bookingRepo.findByPaymentStatus(book1.getPaymentStatus()).size()).isEqualTo(1);
		then(this.bookingRepo.findByPaymentStatus(book2.getPaymentStatus()).size()).isEqualTo(1);
	}

	@Test(expected = TourGuideUnavailableException.class)
	public void testFailureCreateTwoOfferingsWithSameTourGuideAtSameDate()
			throws OfferingDateUnsupportedException, TourGuideUnavailableException, OfferingOutOfRoomException {

		Tour tour1 = new Tour("Yangshan", "Many hotsprings", 3, 0.8, 0.0, 599, 699);
		tour1.setAllowedDates(
				new HashSet<Date>(Arrays.asList(new GregorianCalendar(2017, Calendar.DECEMBER, 9).getTime(),
						new GregorianCalendar(2017, Calendar.DECEMBER, 12).getTime())));

		tourRepo.save(tour1);

		dbManager.createOfferingForTour(tourRepo.findOne(tour1.getId()), tourGuideRepo.findOne(tg1.getId()),
				new ArrayList<Date>(tour1.getAllowedDates()).get(0), "hotel bob", 5, 18);

		dbManager.createOfferingForTour(tourRepo.findOne(tour1.getId()), tourGuideRepo.findOne(tg1.getId()),
				new ArrayList<Date>(tour1.getAllowedDates()).get(0), "hotel cheap", 5, 18);

	}

	@Test(expected = OfferingDateUnsupportedException.class)
	public void testFailureTryToMakeOfferingForUnsupportedDate()
			throws OfferingDateUnsupportedException, TourGuideUnavailableException {

		Tour yangshanTour = new Tour("Yangshan", "Many hotsprings", 3, 0.8, 0.0, 599, 699);
		yangshanTour.setAllowedDates(
				new HashSet<Date>(Arrays.asList(new GregorianCalendar(2017, Calendar.DECEMBER, 9).getTime(),
						new GregorianCalendar(2017, Calendar.DECEMBER, 12).getTime())));

		tourRepo.save(yangshanTour);

		dbManager.createOfferingForTour(tourRepo.findOne(yangshanTour.getId()), tourGuideRepo.findOne(tg1.getId()),
				new GregorianCalendar(2020, Calendar.DECEMBER, 12).getTime(), "hotel bob", 5, 18);
	}

	@Test(expected = OfferingDateUnsupportedException.class)
	public void testFailureTryToMakeOfferingForUnsupportedDayOfWeek()
			throws OfferingOutOfRoomException, OfferingDateUnsupportedException, TourGuideUnavailableException {

		Tour shimenTour = new Tour("Shimen Forest", "Color ponds...", 2, 0.8, 0, 499, 599);
		shimenTour.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(Calendar.TUESDAY, Calendar.SUNDAY)));

		dbManager.createOfferingForTour(shimenTour, tg1, new GregorianCalendar(2017, Calendar.DECEMBER, 4).getTime(),
				"Hotel chep", 4, 20);
		// 2017 December 4 is a monday
	}

	@Test(expected = OfferingOutOfRoomException.class)
	public void testFailureOfferingOutOfRoomWhenBooking()
			throws OfferingDateUnsupportedException, TourGuideUnavailableException, OfferingOutOfRoomException {

		Tour shimenTour = new Tour("Shimen Forest", "Color ponds...", 2, 0.8, 0, 499, 599);
		shimenTour.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));

		int offeringMaxSize = 20;
		Offering shimenOffering = dbManager.createOfferingForTour(shimenTour, tg1, Utils.addDate(now, 100),
				"Hotel chep", 4, offeringMaxSize);

		int numAdults = 20;
		int numChildren = 5;
		int numToddlers = 2;
		dbManager.createBookingForOffering(offeringRepo.findOne(shimenOffering.getId()),
				customerRepo.findOne(c1.getId()), numAdults, numChildren, numToddlers, 0, "kids meal",
				Booking.PAYMENT_CONFIRMED, 1);
	}

	@Test
	public void testSuccessUsingPromoCode() throws OfferingDateUnsupportedException, TourGuideUnavailableException,
			OfferingOutOfRoomException, NoSuchPromoCodeException, PromoForCustomerExceededException,
			PromoCodeUsedUpException, PromoCodeNotForOfferingException {

		Tour shimenTour = new Tour("Shimen Forest", "Color ponds...", 2, 0.8, 0, 499, 599);
		shimenTour.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));

		Offering shimenOffering = dbManager.createOfferingForTour(shimenTour, tg1, Utils.addDate(now, 20), "Hotel chep",
				4, 20);

		int codeUse = 5;
		int maxUsePerCustomer = 2;

		PromoEvent pe = new PromoEvent(new GregorianCalendar(2017, Calendar.DECEMBER, 4).getTime(), "Hi", "TEST_CODE",
				0.5, codeUse, maxUsePerCustomer, shimenOffering);
		promoRepo.save(pe);

		int numAdults = 1;
		int numChildren = 1;
		int numToddlers = 0;

		Booking bk = new Booking(c1, shimenOffering, numAdults, numChildren, numToddlers, 0, "", "");

		dbManager.createBookingForOfferingWithPromoCode(bk, pe.getPromoCode());
	}

	@Test(expected = PromoForCustomerExceededException.class)
	public void testFailTooManyPromoCodeUseOnce() throws OfferingDateUnsupportedException,
			TourGuideUnavailableException, OfferingOutOfRoomException, NoSuchPromoCodeException,
			PromoForCustomerExceededException, PromoCodeUsedUpException, PromoCodeNotForOfferingException {

		Tour shimenTour = new Tour("Shimen Forest", "Color ponds...", 2, 0.8, 0, 499, 599);
		shimenTour.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));

		Offering shimenOffering = dbManager.createOfferingForTour(shimenTour, tg1, Utils.addDate(now, 20), "Hotel chep",
				4, 20);

		int codeUse = 5;
		int maxUsePerCustomer = 2;

		PromoEvent pe = new PromoEvent(Utils.addDate(now, 5), "Hi", "TEST_CODE", 0.5, codeUse, maxUsePerCustomer,
				shimenOffering);
		promoRepo.save(pe);

		int numAdults = 5;
		int numChildren = 5;
		int numToddlers = 5;

		Booking bk = new Booking(c1, shimenOffering, numAdults, numChildren, numToddlers, 0, "", "");

		dbManager.createBookingForOfferingWithPromoCode(bk, pe.getPromoCode());
	}

	@Test(expected = PromoForCustomerExceededException.class)
	public void testFailTooManyPromoCodeUseMulti() throws OfferingDateUnsupportedException,
			TourGuideUnavailableException, OfferingOutOfRoomException, NoSuchPromoCodeException,
			PromoForCustomerExceededException, PromoCodeUsedUpException, PromoCodeNotForOfferingException {

		Tour shimenTour = new Tour("Shimen Forest", "Color ponds...", 2, 0.8, 0, 499, 599);
		shimenTour.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));

		Offering shimenOffering = dbManager.createOfferingForTour(shimenTour, tg1, Utils.addDate(now, 8), "Hotel chep",
				4, 20);

		int codeUse = 5;
		int maxUsePerCustomer = 2;

		PromoEvent pe = new PromoEvent(Utils.addDate(now, 4), "Hi", "TEST_CODE", 0.5, codeUse, maxUsePerCustomer,
				shimenOffering);
		promoRepo.save(pe);

		int numAdults = 2;
		int numChildren = 0;
		int numToddlers = 0;

		Booking bk = new Booking(c2, shimenOffering, numAdults, numChildren, numToddlers, 0, "", "");
		dbManager.createBookingForOfferingWithPromoCode(bk, pe.getPromoCode());

		Booking bk2 = new Booking(c2, shimenOffering, numAdults, numChildren, numToddlers, 0, "", "");
		dbManager.createBookingForOfferingWithPromoCode(bk2, pe.getPromoCode());
	}

	@Test
	public void testIsTourGuideAvailableBetweenDate()
			throws OfferingDateUnsupportedException, TourGuideUnavailableException {
		int tourDurationDays = 2;
		Tour t = new Tour("Shimen Forest", "Color ponds...", tourDurationDays, 0.8, 0, 499, 599);
		t.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));

		Date now = new Date();
		Date yesterday = Utils.addDate(now, -1);
		Date tomorrow = Utils.addDate(now, 1);

		Date twoDaysAgo = Utils.addDate(now, -2);
		Date twoDaysLater = Utils.addDate(now, 2);

		Offering x = dbManager.createOfferingForTour(t, tg1, now, "Hotel chep", 4, 20);

		assertFalse(dbManager.isTourGuideAvailableBetweenDate(tg1, twoDaysAgo, twoDaysLater));
		assertFalse(dbManager.isTourGuideAvailableBetweenDate(tg1, twoDaysAgo, tomorrow));
		assertFalse(dbManager.isTourGuideAvailableBetweenDate(tg1, yesterday, twoDaysLater));
		assertFalse(dbManager.isTourGuideAvailableBetweenDate(tg1, yesterday, tomorrow));
		assertFalse(dbManager.isTourGuideAvailableBetweenDate(tg1, now, tomorrow));
		assertFalse(dbManager.isTourGuideAvailableBetweenDate(tg1, yesterday, now));

	}

	@Test
	public void testIsTourGuideAvailableBetweenDateExcludeOffering()
			throws OfferingDateUnsupportedException, TourGuideUnavailableException {
		int tourDurationDays = 2;
		Tour t = new Tour("Shimen Forest", "Color ponds...", tourDurationDays, 0.8, 0, 499, 599);
		t.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));

		Date now = new Date();
		Date yesterday = Utils.addDate(now, -1);
		Date tomorrow = Utils.addDate(now, 1);

		Date twoDaysAgo = Utils.addDate(now, -2);
		Date twoDaysLater = Utils.addDate(now, 2);

		// this cancelled offerings shouldn't even be considered
		Offering cancel = dbManager.createOfferingForTour(t, tg1, now, "Hotel chep", 4, 20);
		cancel.setStatus(Offering.STATUS_CANCELLED);
		offeringRepo.save(cancel);

		Offering x = dbManager.createOfferingForTour(t, tg1, now, "Hotel chep", 4, 20);

		assertTrue(dbManager.isTourGuideAvailableBetweenDateExcludeOffering(tg1, twoDaysAgo, twoDaysLater, x));
		assertTrue(dbManager.isTourGuideAvailableBetweenDateExcludeOffering(tg1, twoDaysAgo, tomorrow, x));
		assertTrue(dbManager.isTourGuideAvailableBetweenDateExcludeOffering(tg1, yesterday, twoDaysLater, x));
		assertTrue(dbManager.isTourGuideAvailableBetweenDateExcludeOffering(tg1, yesterday, tomorrow, x));

		Offering farAway = dbManager.createOfferingForTour(t, tg1, Utils.addDate(now, 100), "Hotel chep", 4, 20);
		assertFalse(dbManager.isTourGuideAvailableBetweenDateExcludeOffering(tg1, twoDaysAgo, twoDaysLater, farAway));
		assertFalse(dbManager.isTourGuideAvailableBetweenDateExcludeOffering(tg1, twoDaysAgo, tomorrow, farAway));
		assertFalse(dbManager.isTourGuideAvailableBetweenDateExcludeOffering(tg1, yesterday, twoDaysLater, farAway));
		assertFalse(dbManager.isTourGuideAvailableBetweenDateExcludeOffering(tg1, yesterday, tomorrow, farAway));
		assertFalse(dbManager.isTourGuideAvailableBetweenDateExcludeOffering(tg1, yesterday,
				Utils.addDate(x.getStartDate(), x.getTour().getDays()), farAway));
		assertFalse(dbManager.isTourGuideAvailableBetweenDateExcludeOffering(tg1, now, tomorrow, farAway));
	}

	@Test
	public void testSuccessCreateNewLogin() throws UsernameTakenException {
		LoginUser test = dbManager.createNewLogin("Johnny", "hunter2");
		loginUserRepo.delete(test);
	}

	@Test
	public void testFailureCreateLoginWithDuplicateUsername() throws UsernameTakenException {
		LoginUser test = dbManager.createNewLogin("Johnny", "hunter2");
		try {
			dbManager.createNewLogin("Johnny", "hunter2");
		} catch (UsernameTakenException e) {
			loginUserRepo.delete(test);
			return;
		}
		fail();
	}

	@Test
	public void testSuccessCountNumOfferingsForTour()
			throws OfferingDateUnsupportedException, TourGuideUnavailableException {
		Tour t = new Tour("Shimen Forest", "Color ponds...", 2, 0.8, 0, 499, 599);
		t.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));
		tourRepo.save(t);
		assertEquals(0, dbManager.countNumOfferingsForTour(t));
		dbManager.createOfferingForTour(t, tg1, new Date(), "Hotel chep", 4, 20);
		assertEquals(1, dbManager.countNumOfferingsForTour(t));

	}

	@Test(expected = NoSuchPromoCodeException.class)
	public void testFailureValidatePromoCodeDoesntExist() throws PromoCodeUsedUpException, NoSuchPromoCodeException {
		dbManager.validatePromoCode("lol");
	}

	@Test(expected = PromoCodeUsedUpException.class)
	public void testFailureValidatePromoCodeUsedUp() throws PromoCodeUsedUpException, NoSuchPromoCodeException,
			OfferingDateUnsupportedException, TourGuideUnavailableException {
		Tour shimenTour = new Tour("Shimen Forest", "Color ponds...", 2, 0.8, 0, 499, 599);
		shimenTour.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));

		Offering shimenOffering = dbManager.createOfferingForTour(shimenTour, tg1, now, "Hotel chep", 4, 20);

		int codeUse = 0;
		int maxUsePerCustomer = 2;

		PromoEvent pe = new PromoEvent(Utils.addDate(now, -1), "Hi", "TEST_CODE", 0.5, codeUse, maxUsePerCustomer,
				shimenOffering);
		promoRepo.save(pe);

		dbManager.validatePromoCode(pe.getPromoCode());
	}

	@Test(expected = PromoCodeNotForOfferingException.class)
	public void testFailureValidatePromoCodeNotForOffering() throws OfferingDateUnsupportedException,
			TourGuideUnavailableException, PromoCodeUsedUpException, NoSuchPromoCodeException,
			OfferingOutOfRoomException, PromoForCustomerExceededException, PromoCodeNotForOfferingException {

		Date now = new Date();
		Tour tour1 = new Tour("Shimen Forest", "Color ponds...", 2, 0.8, 0, 499, 599);
		tour1.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));

		Offering o1 = dbManager.createOfferingForTour(tour1, tg1, Utils.addDate(now, 20), "Hotel chep", 4, 20);

		int codeUse = 4;
		int maxUsePerCustomer = 2;

		PromoEvent pe1 = new PromoEvent(Utils.addDate(now, 1), "Hi", "TEST_CODE", 0.5, codeUse, maxUsePerCustomer, o1);
		promoRepo.save(pe1);

		// now a second one
		Tour tour2 = new Tour("Shimen Forest", "Color ponds...", 2, 0.8, 0, 499, 599);
		tour2.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(Calendar.MONDAY, Calendar.SUNDAY)));

		Offering o2 = dbManager.createOfferingForTour(tour2, tg1, Utils.addDate(now, 50), "Hotel chep", 4, 20);

		PromoEvent pe2 = new PromoEvent(Utils.addDate(now, 1), "Hi", "asdf", 0.5, codeUse, maxUsePerCustomer, o2);
		promoRepo.save(pe2);

		Booking bookingUsingWrongPromoCode = bookingRepo
				.save(new Booking(c1, o1, 1, 1, 0, 0, "", Booking.PAYMENT_PENDING, 1, pe2.getPromoCode()));
		dbManager.createBookingForOfferingWithPromoCode(bookingUsingWrongPromoCode, pe2.getPromoCode());
	}

	@Test
	public void testSuccessEditTour() throws OfferingDateUnsupportedException, TourGuideUnavailableException {
		Tour tour1 = new Tour("Shimen Forest", "Color ponds...", 2, 0.8, 0, 499, 599);
		tour1.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));
		Offering o1 = dbManager.createOfferingForTour(tour1, tg1, Utils.addDate(now, 20), "Hotel chep", 4, 20);
		dbManager.editOfferingTorTour(o1);
	}

	@Test(expected = OfferingDateUnsupportedException.class)
	public void testFailureEditTourOfferingDateUnsupported()
			throws OfferingDateUnsupportedException, TourGuideUnavailableException {
		Tour tour1 = new Tour("Shimen Forest", "Color ponds...", 2, 0.8, 0, 499, 599);
		tour1.setAllowedDates(new HashSet<Date>(Arrays.asList(now)));
		Offering o1 = dbManager.createOfferingForTour(tour1, tg1, now, "Hotel chep", 4, 20);

		o1.setStartDate(Utils.addDate(now, 1));
		dbManager.editOfferingTorTour(o1);
	}

	@Test(expected = TourGuideUnavailableException.class)
	public void testFailureEditTourOfferingTourGuideUnavailable()
			throws OfferingDateUnsupportedException, TourGuideUnavailableException {
		Tour tour1 = new Tour("Shimen Forest", "Color ponds...", 2, 0.8, 0, 499, 599);
		tour1.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));
		Offering o1 = dbManager.createOfferingForTour(tour1, tg1, now, "Hotel chep", 4, 20);
		Offering o2 = dbManager.createOfferingForTour(tour1, tg1, Utils.addDate(now, 10), "Hotel chep", 4, 20);

		o1.setStartDate(Utils.addDate(now, 10));
		dbManager.editOfferingTorTour(o1);
	}

	@Test
	public void testAuthenticate() {
		then(authentication.authenticate("admin", "Q1w2e3r4")).isTrue();
		then(authentication.authenticate("asfas", "Q1w2e3r4")).isFalse();
		then(authentication.authenticate("admin", "asdasd")).isFalse();
	}

}
