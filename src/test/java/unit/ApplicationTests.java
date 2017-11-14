package unit;

import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import comp3111.Application;
import comp3111.data.DBManager;
import comp3111.data.model.Booking;
import comp3111.data.model.Customer;
import comp3111.data.model.Offering;
import comp3111.data.model.Tour;
import comp3111.data.model.TourGuide;
import comp3111.data.repo.BookingRepository;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.LoginUserRepository;
import comp3111.data.repo.NonFAQQueryRepository;
import comp3111.data.repo.OfferingRepository;
import comp3111.data.repo.TourGuideRepository;
import comp3111.data.repo.TourRepository;
import comp3111.input.exceptions.OfferingDateUnsupportedException;
import comp3111.input.exceptions.OfferingDayOfWeekUnsupportedException;
import comp3111.input.exceptions.OfferingOutOfRoomException;
import comp3111.input.exceptions.TourGuideUnavailableException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ApplicationTests {

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
	private DBManager actionManager;

	Customer c1, c2;
	TourGuide tg1, tg2;

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
		actionManager.deleteAll();

		c1 = new Customer(C1_NAME, 30);
		c2 = new Customer(C2_NAME, 30);
		customerRepo.save(c1);
		customerRepo.save(c2);

		tg1 = new TourGuide(TG1_NAME, TG1_LINE_USERNAME);
		tg2 = new TourGuide(TG2_NAME, TG2_LINE_USERNAME);
		tourGuideRepo.save(tg1);
		tourGuideRepo.save(tg2);
	}

	@After
	public void tearDown() {
		actionManager.deleteAll();
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
	public void testSuccessCreateOfferingAndBookingForUnsavedEntities() throws OfferingOutOfRoomException,
			OfferingDateUnsupportedException, TourGuideUnavailableException {
		TourGuide anonTg = new TourGuide("lucy", "LINEID123");
		Customer anonCust = new Customer("morgan freeman", 35);
		Tour anonTour = new Tour("Shimen Forest", "Color ponds...", 2, 0.8, 0, 499, 599);
		anonTour.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(Calendar.MONDAY, Calendar.SUNDAY)));

		// testing with unsaved offering object (peppaPig, shimenTour)
		Offering anonOffering = actionManager.createOfferingForTour(anonTour, anonTg,
				new GregorianCalendar(2017, Calendar.DECEMBER, 4).getTime(), "Hotel chep", 4, 20);
		Booking anonBooking = actionManager.createBookingForOffering(anonOffering, anonCust, 5, 2, 3, 0, "no smoking",
				Booking.PAYMENT_CONFIRMED);

		then(this.tourGuideRepo.findByName(anonTg.getName()).size()).isEqualTo(1);
		then(this.customerRepo.findByName(anonCust.getName()).size()).isEqualTo(1);
		then(this.tourRepo.findByTourName(anonTour.getTourName()).size()).isEqualTo(1);

		then(this.offeringRepo.findByHotelName(anonOffering.getHotelName()).size()).isEqualTo(1);
		then(this.bookingRepo.findByPaymentStatus(anonBooking.getPaymentStatus()).size()).isEqualTo(1);
	}

	@Test
	public void testSuccessCreateOfferingAndBookingForSavedEntities() throws OfferingOutOfRoomException,
			OfferingDateUnsupportedException, TourGuideUnavailableException {

		Tour tour1 = new Tour("Yangshan", "Many hotsprings", 3, 0.8, 0.0, 599, 699);
		tour1.setAllowedDates(
				new HashSet<Date>(Arrays.asList(new GregorianCalendar(2017, Calendar.DECEMBER, 9).getTime(),
						new GregorianCalendar(2017, Calendar.DECEMBER, 12).getTime())));

		tourRepo.save(tour1);

		Offering yangShanOffering = actionManager.createOfferingForTour(tourRepo.findOne(tour1.getId()),
				tourGuideRepo.findOne(tg1.getId()), new ArrayList<Date>(tour1.getAllowedDates()).get(0), "hotel bob", 5,
				18);
		Booking booking = actionManager.createBookingForOffering(offeringRepo.findOne(yangShanOffering.getId()),
				customerRepo.findOne(c1.getId()), 3, 4, 2, 0, "kids meal", Booking.PAYMENT_PENDING);

		then(this.offeringRepo.findByHotelName(yangShanOffering.getHotelName()).size()).isEqualTo(1);
		then(this.bookingRepo.findByPaymentStatus(booking.getPaymentStatus()).size()).isEqualTo(1);
	}

	@Test
	public void testSuccessTwoCustomersMakeBookingForATour() throws OfferingDateUnsupportedException,
			TourGuideUnavailableException, OfferingOutOfRoomException {
		Tour tour1 = new Tour("Yangshan", "Many hotsprings", 3, 0.8, 0.0, 599, 699);
		tour1.setAllowedDates(
				new HashSet<Date>(Arrays.asList(new GregorianCalendar(2017, Calendar.DECEMBER, 9).getTime(),
						new GregorianCalendar(2017, Calendar.DECEMBER, 12).getTime())));

		tourRepo.save(tour1);

		Offering yangShanOffering = actionManager.createOfferingForTour(tourRepo.findOne(tour1.getId()),
				tourGuideRepo.findOne(tg1.getId()), new ArrayList<Date>(tour1.getAllowedDates()).get(0), "hotel bob", 5,
				18);

		actionManager.createBookingForOffering(offeringRepo.findOne(yangShanOffering.getId()),
				customerRepo.findOne(c1.getId()), 3, 4, 2, 0, "kids meal", Booking.PAYMENT_PENDING);

		actionManager.createBookingForOffering(offeringRepo.findOne(yangShanOffering.getId()),
				customerRepo.findOne(c2.getId()), 3, 4, 2, 0, "no smoking meal", Booking.PAYMENT_PENDING);

		then(this.offeringRepo.findByHotelName(yangShanOffering.getHotelName()).size()).isEqualTo(1);
		then(this.bookingRepo.findByOffering(yangShanOffering).size()).isEqualTo(2);
	}

	@Test
	public void testSuccessCreateTwoOfferingsWithDifferentTourGuidesForATour() throws OfferingDateUnsupportedException,
			TourGuideUnavailableException, OfferingOutOfRoomException {

		Tour tour1 = new Tour("Yangshan", "Many hotsprings", 3, 0.8, 0.0, 599, 699);
		tour1.setAllowedDates(
				new HashSet<Date>(Arrays.asList(new GregorianCalendar(2017, Calendar.DECEMBER, 9).getTime(),
						new GregorianCalendar(2017, Calendar.DECEMBER, 12).getTime())));

		tourRepo.save(tour1);

		Offering offering1 = actionManager.createOfferingForTour(tourRepo.findOne(tour1.getId()),
				tourGuideRepo.findOne(tg1.getId()), new ArrayList<Date>(tour1.getAllowedDates()).get(0), "hotel bob", 5,
				18);
		Booking book1 = actionManager.createBookingForOffering(offeringRepo.findOne(offering1.getId()),
				customerRepo.findOne(c1.getId()), 3, 4, 2, 0, "kids meal", Booking.PAYMENT_PENDING);

		Offering offering2 = actionManager.createOfferingForTour(tourRepo.findOne(tour1.getId()),
				tourGuideRepo.findOne(tg2.getId()), new ArrayList<Date>(tour1.getAllowedDates()).get(0), "hotel cheap",
				5, 18);
		Booking book2 = actionManager.createBookingForOffering(offeringRepo.findOne(offering2.getId()),
				customerRepo.findOne(c2.getId()), 3, 4, 2, 0, "kids meal", Booking.PAYMENT_CONFIRMED);

		then(this.offeringRepo.findByHotelName(offering1.getHotelName()).size()).isEqualTo(1);
		then(this.offeringRepo.findByHotelName(offering2.getHotelName()).size()).isEqualTo(1);
		then(this.bookingRepo.findByPaymentStatus(book1.getPaymentStatus()).size()).isEqualTo(1);
		then(this.bookingRepo.findByPaymentStatus(book2.getPaymentStatus()).size()).isEqualTo(1);
	}

	@Test(expected = TourGuideUnavailableException.class)
	public void testFailureCreateTwoOfferingsWithSameTourGuideAtSameDate() throws OfferingDateUnsupportedException,
			TourGuideUnavailableException, OfferingOutOfRoomException {

		Tour tour1 = new Tour("Yangshan", "Many hotsprings", 3, 0.8, 0.0, 599, 699);
		tour1.setAllowedDates(
				new HashSet<Date>(Arrays.asList(new GregorianCalendar(2017, Calendar.DECEMBER, 9).getTime(),
						new GregorianCalendar(2017, Calendar.DECEMBER, 12).getTime())));

		tourRepo.save(tour1);

		actionManager.createOfferingForTour(tourRepo.findOne(tour1.getId()), tourGuideRepo.findOne(tg1.getId()),
				new ArrayList<Date>(tour1.getAllowedDates()).get(0), "hotel bob", 5, 18);

		actionManager.createOfferingForTour(tourRepo.findOne(tour1.getId()), tourGuideRepo.findOne(tg1.getId()),
				new ArrayList<Date>(tour1.getAllowedDates()).get(0), "hotel cheap", 5, 18);

	}

	@Test(expected = OfferingDateUnsupportedException.class)
	public void testFailureTryToMakeOfferingForUnsupportedDate() throws OfferingDateUnsupportedException,
			TourGuideUnavailableException {

		Tour yangshanTour = new Tour("Yangshan", "Many hotsprings", 3, 0.8, 0.0, 599, 699);
		yangshanTour.setAllowedDates(
				new HashSet<Date>(Arrays.asList(new GregorianCalendar(2017, Calendar.DECEMBER, 9).getTime(),
						new GregorianCalendar(2017, Calendar.DECEMBER, 12).getTime())));

		tourRepo.save(yangshanTour);

		actionManager.createOfferingForTour(tourRepo.findOne(yangshanTour.getId()), tourGuideRepo.findOne(tg1.getId()),
				new GregorianCalendar(2020, Calendar.DECEMBER, 12).getTime(), "hotel bob", 5, 18);
	}

	@Test(expected = OfferingDateUnsupportedException.class)
	public void testFailureTryToMakeOfferingForUnsupportedDayOfWeek() throws OfferingOutOfRoomException,
			OfferingDateUnsupportedException, TourGuideUnavailableException {

		Tour shimenTour = new Tour("Shimen Forest", "Color ponds...", 2, 0.8, 0, 499, 599);
		shimenTour.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(Calendar.TUESDAY, Calendar.SUNDAY)));

		actionManager.createOfferingForTour(shimenTour, tg1,
				new GregorianCalendar(2017, Calendar.DECEMBER, 4).getTime(), "Hotel chep", 4, 20);
		// 2017 December 4 is a monday
	}

	@Test(expected = OfferingOutOfRoomException.class)
	public void testFailureOfferingOutOfRoomWhenBooking() throws OfferingDateUnsupportedException,
			TourGuideUnavailableException, OfferingOutOfRoomException {

		Tour shimenTour = new Tour("Shimen Forest", "Color ponds...", 2, 0.8, 0, 499, 599);
		shimenTour.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(Calendar.MONDAY, Calendar.SUNDAY)));

		int offeringMaxSize = 20;
		Offering shimenOffering = actionManager.createOfferingForTour(shimenTour, tg1,
				new GregorianCalendar(2017, Calendar.DECEMBER, 4).getTime(), "Hotel chep", 4, offeringMaxSize);

		int numAdults = 20;
		int numChildren = 5;
		int numToddlers = 2;
		actionManager.createBookingForOffering(offeringRepo.findOne(shimenOffering.getId()),
				customerRepo.findOne(c1.getId()), numAdults, numChildren, numToddlers, 0, "kids meal",
				Booking.PAYMENT_CONFIRMED);
	}

}
