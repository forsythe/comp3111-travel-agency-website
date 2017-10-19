package springboot;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import comp3111.ActionManager;
import comp3111.Application;
import comp3111.exceptions.OfferingDateUnsupportedException;
import comp3111.exceptions.OfferingDayOfWeekUnsupportedException;
import comp3111.exceptions.OfferingOutOfRoomException;
import comp3111.exceptions.TourGuideUnavailableException;
import comp3111.model.Customer;
import comp3111.model.CustomerOffering;
import comp3111.model.Offering;
import comp3111.model.Tour;
import comp3111.model.TourGuide;
import comp3111.repo.CustomerOfferingRepository;
import comp3111.repo.CustomerRepository;
import comp3111.repo.LoginUserRepository;
import comp3111.repo.OfferingRepository;
import comp3111.repo.TourGuideRepository;
import comp3111.repo.TourRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ApplicationTests {

	@Autowired
	private CustomerRepository customerRepo;
	@Autowired
	private CustomerOfferingRepository customerOfferingRepo;
	@Autowired
	private LoginUserRepository loginUserRepo;
	@Autowired
	private OfferingRepository offeringRepo;
	@Autowired
	private TourRepository tourRepo;
	@Autowired
	private TourGuideRepository tourGuideRepo;
	@Autowired
	private ActionManager actionManager;

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
		CustomerOffering booking = actionManager.createBookingForOffering(shimenOffering, peppaPig, 5, 2, 3, 0,
				"no smoking", "pending");

		then(this.tourGuideRepo.findByName("Amber").size()).isEqualTo(1);
		then(this.customerRepo.findByName("Peppa Pig").size()).isEqualTo(1);
		then(this.tourRepo.findByTourName("Shimen Forest").size()).isEqualTo(1);
		then(this.tourGuideRepo.findByName("Amber").size()).isEqualTo(1);

		then(this.offeringRepo.findByHotelName(shimenOffering.getHotelName()).size()).isEqualTo(1);
		then(this.customerOfferingRepo.findByPaymentStatus(booking.getPaymentStatus()).size()).isEqualTo(1);

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
		CustomerOffering booking = actionManager.createBookingForOffering(
				offeringRepo.findOne(yangShanOffering.getId()), customerRepo.findOne(emilyElephant.getId()), 3, 4, 2, 0,
				"kids meal", "pending");

		then(this.offeringRepo.findByHotelName(yangShanOffering.getHotelName()).size()).isEqualTo(1);
		then(this.customerOfferingRepo.findByPaymentStatus(booking.getPaymentStatus()).size()).isEqualTo(1);
	}

	@Test
	public void testFailureTryToAssignBusyTourGuide() {
		// want exception
	}

	@Test
	public void testFailureTryToMakeOfferingForUnsupportedDate() {
		// want exception
	}

	@Test
	public void testFailureTryToMakeOfferingForUnsupportedDayOfWeek() {
		// want exception
	}

	@Test
	public void testFailureOfferingOutOfRoomWhenBooking() {

	}

}
