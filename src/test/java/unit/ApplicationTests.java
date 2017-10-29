package unit;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Ignore;
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
	private DBManager actionManager;

	@Before // called before each test
	public void setup() {
		actionManager.deleteAll();
	}

	@Test
	public void testSuccessShouldHaveOneLoginUser() {
		then(this.loginUserRepo.count()).isEqualTo(1);
	}

	@Test
	public void testSuccessCreateOfferingAndBookingForUnsavedEntities() throws OfferingOutOfRoomException,
			OfferingDateUnsupportedException, OfferingDayOfWeekUnsupportedException, TourGuideUnavailableException {
		TourGuide amber = new TourGuide("Amber", "LINEID123");
		Customer peppaPig = new Customer("Peppa Pig", 35);
		Tour shimenTour = new Tour("Shimen Forest", "Color ponds...", 2, 0.8, 0, 499, 599);
		shimenTour.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(Calendar.MONDAY, Calendar.SUNDAY)));

		// testing with unsaved offering object (peppaPig, shimenTour)
		Offering shimenOffering = actionManager.createOfferingForTour(shimenTour, amber,
				new GregorianCalendar(2017, Calendar.DECEMBER, 4).getTime(), "Hotel chep", 4, 20);
		Booking booking = actionManager.createBookingForOffering(shimenOffering, peppaPig, 5, 2, 3, 0, "no smoking",
				"pending");

		then(this.tourGuideRepo.findByName("Amber").size()).isEqualTo(1);
		then(this.customerRepo.findByName("Peppa Pig").size()).isEqualTo(1);
		then(this.tourRepo.findByTourName("Shimen Forest").size()).isEqualTo(1);
		then(this.tourGuideRepo.findByName("Amber").size()).isEqualTo(1);

		then(this.offeringRepo.findByHotelName(shimenOffering.getHotelName()).size()).isEqualTo(1);
		then(this.bookingRepo.findByPaymentStatus(booking.getPaymentStatus()).size()).isEqualTo(1);

	}

	@Test
	public void testSuccessCreateOfferingAndBookingForSavedEntities() throws OfferingOutOfRoomException,
			OfferingDateUnsupportedException, OfferingDayOfWeekUnsupportedException, TourGuideUnavailableException {

		Customer emilyElephant = new Customer("Emily Elephant", 36);
		TourGuide bob = new TourGuide("Bob", "LINEID1211");
		customerRepo.save(emilyElephant);
		tourGuideRepo.save(bob);

		Tour yangshanTour = new Tour("Yangshan", "Many hotsprings", 3, 0.8, 0.0, 599, 699);
		yangshanTour.setAllowedDates(
				new HashSet<Date>(Arrays.asList(new GregorianCalendar(2017, Calendar.DECEMBER, 9).getTime(),
						new GregorianCalendar(2017, Calendar.DECEMBER, 12).getTime())));

		tourRepo.save(yangshanTour);

		then(this.customerRepo.findByName("Emily Elephant").size()).isEqualTo(1);
		then(this.tourRepo.findByTourName("Yangshan").size()).isEqualTo(1);
		then(this.tourGuideRepo.findByName("Bob").size()).isEqualTo(1);

		Offering yangShanOffering = actionManager.createOfferingForTour(tourRepo.findOne(yangshanTour.getId()),
				tourGuideRepo.findOne(bob.getId()), new ArrayList<Date>(yangshanTour.getAllowedDates()).get(0),
				"hotel bob", 5, 18);
		Booking booking = actionManager.createBookingForOffering(offeringRepo.findOne(yangShanOffering.getId()),
				customerRepo.findOne(emilyElephant.getId()), 3, 4, 2, 0, "kids meal", "pending");

		then(this.offeringRepo.findByHotelName(yangShanOffering.getHotelName()).size()).isEqualTo(1);
		then(this.bookingRepo.findByPaymentStatus(booking.getPaymentStatus()).size()).isEqualTo(1);
	}

	@Test
	public void testSuccessCreateTwoOfferingsWithSameTourGuideForATour() throws OfferingDateUnsupportedException,
			OfferingDayOfWeekUnsupportedException, TourGuideUnavailableException, OfferingOutOfRoomException {
		Customer cust1 = new Customer("Emily Elephant", 36);
		Customer cust2 = new Customer("Johnny", 40);

		TourGuide tg1 = new TourGuide("Bob", "LINEID1211");

		customerRepo.save(cust1);
		customerRepo.save(cust2);
		tourGuideRepo.save(tg1);

		Tour tour1 = new Tour("Yangshan", "Many hotsprings", 3, 0.8, 0.0, 599, 699);
		tour1.setAllowedDates(
				new HashSet<Date>(Arrays.asList(new GregorianCalendar(2017, Calendar.DECEMBER, 9).getTime(),
						new GregorianCalendar(2017, Calendar.DECEMBER, 12).getTime())));

		tourRepo.save(tour1);

		then(this.customerRepo.findByName("Emily Elephant").size()).isEqualTo(1);
		then(this.customerRepo.findByName("Johnny").size()).isEqualTo(1);
		then(this.tourRepo.findByTourName("Yangshan").size()).isEqualTo(1);
		then(this.tourGuideRepo.findByName("Bob").size()).isEqualTo(1);

		Offering offering1 = actionManager.createOfferingForTour(tourRepo.findOne(tour1.getId()),
				tourGuideRepo.findOne(tg1.getId()), new ArrayList<Date>(tour1.getAllowedDates()).get(0),
				"hotel bob", 5, 18);
		Booking booking1 = actionManager.createBookingForOffering(offeringRepo.findOne(offering1.getId()),
				customerRepo.findOne(cust1.getId()), 3, 4, 2, 0, "kids meal", "pending");

		Offering offering2 = actionManager.createOfferingForTour(tourRepo.findOne(tour1.getId()),
				tourGuideRepo.findOne(tg1.getId()), new ArrayList<Date>(tour1.getAllowedDates()).get(0),
				"hotel cheap", 5, 18);
		Booking booking2 = actionManager.createBookingForOffering(offeringRepo.findOne(offering2.getId()),
				customerRepo.findOne(cust2.getId()), 3, 4, 2, 0, "kids meal", "pendiasdfang");

		then(this.offeringRepo.findByHotelName(offering1.getHotelName()).size()).isEqualTo(1);
		then(this.offeringRepo.findByHotelName(offering2.getHotelName()).size()).isEqualTo(1);
		then(this.bookingRepo.findByPaymentStatus(booking1.getPaymentStatus()).size()).isEqualTo(1);
		then(this.bookingRepo.findByPaymentStatus(booking2.getPaymentStatus()).size()).isEqualTo(1);

	}

	@Test
	public void testSuccessCreateTwoOfferingsWithDifferentTourGuidesForATour() throws OfferingDateUnsupportedException,
			OfferingDayOfWeekUnsupportedException, TourGuideUnavailableException, OfferingOutOfRoomException {
		Customer cust1 = new Customer("Emily Elephant", 36);
		Customer cust2 = new Customer("Johnny", 40);

		TourGuide tg1 = new TourGuide("Bob", "LINEID1211");
		TourGuide tg2 = new TourGuide("tg", "LINEID1211");

		customerRepo.save(cust1);
		customerRepo.save(cust2);

		tourGuideRepo.save(tg1);
		tourGuideRepo.save(tg2);

		Tour tour1 = new Tour("Yangshan", "Many hotsprings", 3, 0.8, 0.0, 599, 699);
		tour1.setAllowedDates(
				new HashSet<Date>(Arrays.asList(new GregorianCalendar(2017, Calendar.DECEMBER, 9).getTime(),
						new GregorianCalendar(2017, Calendar.DECEMBER, 12).getTime())));

		tourRepo.save(tour1);

		then(this.customerRepo.findByName("Emily Elephant").size()).isEqualTo(1);
		then(this.customerRepo.findByName("Johnny").size()).isEqualTo(1);
		then(this.tourRepo.findByTourName("Yangshan").size()).isEqualTo(1);
		then(this.tourGuideRepo.findByName("Bob").size()).isEqualTo(1);
		then(this.tourGuideRepo.findByName("tg").size()).isEqualTo(1);

		Offering offering1 = actionManager.createOfferingForTour(tourRepo.findOne(tour1.getId()),
				tourGuideRepo.findOne(tg1.getId()), new ArrayList<Date>(tour1.getAllowedDates()).get(0),
				"hotel bob", 5, 18);
		Booking book1 = actionManager.createBookingForOffering(offeringRepo.findOne(offering1.getId()),
				customerRepo.findOne(cust1.getId()), 3, 4, 2, 0, "kids meal", "pending");

		Offering offering2 = actionManager.createOfferingForTour(tourRepo.findOne(tour1.getId()),
				tourGuideRepo.findOne(tg2.getId()),
				new ArrayList<Date>(tour1.getAllowedDates()).get(0), "hotel cheap", 5, 18);
		Booking book2 = actionManager.createBookingForOffering(offeringRepo.findOne(offering2.getId()),
				customerRepo.findOne(cust2.getId()), 3, 4, 2, 0, "kids meal", "pendiasdfang");

		then(this.offeringRepo.findByHotelName(offering1.getHotelName()).size()).isEqualTo(1);
		then(this.offeringRepo.findByHotelName(offering2.getHotelName()).size()).isEqualTo(1);
		then(this.bookingRepo.findByPaymentStatus(book1.getPaymentStatus()).size()).isEqualTo(1);
		then(this.bookingRepo.findByPaymentStatus(book2.getPaymentStatus()).size()).isEqualTo(1);
	}

	@Ignore
	@Test(expected = TourGuideUnavailableException.class)
	public void testFailureTryToAssignBusyTourGuide() {
		// want exception
	}

	@Ignore
	@Test
	public void testFailureTryToMakeOfferingForUnsupportedDate() {
		// want exception
	}

	@Ignore
	@Test
	public void testFailureTryToMakeOfferingForUnsupportedDayOfWeek() {
		// want exception
	}

	@Ignore
	@Test
	public void testFailureOfferingOutOfRoomWhenBooking() {

	}

}
