package unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import comp3111.Application;
import comp3111.LineMessenger;
import comp3111.data.DBManager;
import comp3111.data.model.Booking;
import comp3111.data.model.Customer;
import comp3111.data.model.NonFAQQuery;
import comp3111.data.model.Offering;
import comp3111.data.model.Tour;
import comp3111.data.model.TourGuide;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.NonFAQQueryRepository;
import comp3111.data.repo.TourGuideRepository;
import comp3111.data.repo.TourRepository;
import comp3111.input.exceptions.OfferingDateUnsupportedException;
import comp3111.input.exceptions.OfferingDayOfWeekUnsupportedException;
import comp3111.input.exceptions.OfferingOutOfRoomException;
import comp3111.input.exceptions.TourGuideUnavailableException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class LineInteractionTests {

	@Autowired
	private CustomerRepository customerRepo;
	@Autowired
	private TourRepository tourRepo;
	@Autowired
	private TourGuideRepository tourGuideRepo;
	@Autowired
	private NonFAQQueryRepository nonFAQQueryRepo;
	@Autowired
	private LineMessenger lineMessenger;
	@Autowired
	private DBManager actionManager;

	@After
	public void tearDown() {
		actionManager.deleteAll();
	}

	@Test
	public void testSuccessSendMessageToHengsPhone() {
		Customer heng = new Customer("heng", "U6934790c40beeed33b8b89fa359aa9cf", "12312341234", 20, "A1234563");
		heng = customerRepo.save(heng);
		boolean recieved200ok = lineMessenger.sendToUser(heng.getLineId(), "hi heng's phone, this is test case", true);
		assertTrue(recieved200ok);
		assertEquals(LineMessenger.getAndResetCount(), 1);
		customerRepo.delete(heng);
	}

	@Test
	public void testSuccessSendMessageToKVsPhone() {
		Customer kv = new Customer("KV", "Ufbae1ebc457e163d0f351c1865daccf5", "12312341234", 20, "A1234563");
		kv = customerRepo.save(kv);
		boolean recieved200ok = lineMessenger.sendToUser(kv.getLineId(), "hi kv's phone, this is test case", true);
		assertTrue(recieved200ok);
		assertEquals(LineMessenger.getAndResetCount(), 1);
		customerRepo.delete(kv);
	}

	@Test
	public void testSuccessSendMessageToOfferingParticipants() throws OfferingDateUnsupportedException,
			OfferingDayOfWeekUnsupportedException, TourGuideUnavailableException, OfferingOutOfRoomException {
		Customer heng = new Customer("heng", "U6934790c40beeed33b8b89fa359aa9cf", "12312341234", 20, "A1234563");
		heng = customerRepo.save(heng);
		Customer kv = new Customer("KV", "Ufbae1ebc457e163d0f351c1865daccf5", "12312341234", 20, "A1234563");
		kv = customerRepo.save(kv);

		Tour comp3111Tour = new Tour("comp3111h", "learn about design patterns", 3, 0.8, 0.0, 599, 699);
		comp3111Tour.setAllowedDates(
				new HashSet<Date>(Arrays.asList(new GregorianCalendar(2017, Calendar.DECEMBER, 9).getTime(),
						new GregorianCalendar(2017, Calendar.DECEMBER, 12).getTime())));

		tourRepo.save(comp3111Tour);

		TourGuide tg1 = new TourGuide("professor kim", "line123");
		tg1 = tourGuideRepo.save(tg1);

		Offering offering = actionManager.createOfferingForTour(comp3111Tour, tg1,
				new ArrayList<Date>(comp3111Tour.getAllowedDates()).get(0), "hotel bob", 5, 18);
		Booking hengBooking = actionManager.createBookingForOffering(offering, heng, 3, 4, 2, 0, "kids meal",
				Booking.PAYMENT_PENDING);
		Booking kvBooking = actionManager.createBookingForOffering(offering, kv, 3, 4, 2, 0, "kids meal",
				Booking.PAYMENT_PENDING);

		boolean status = lineMessenger.sendToOffering(offering, "send to all participants in offering test case");
		assertTrue(status);
	}

	@Test
	public void testSuccessCanPersistNonFAQQueryAndRespond() {
		Customer heng = new Customer("heng", "U6934790c40beeed33b8b89fa359aa9cf", "12312341234", 20, "A1234563");
		heng = customerRepo.save(heng);
		NonFAQQuery question = new NonFAQQuery("what is spaghet", heng);
		question = nonFAQQueryRepo.save(question);
		assertTrue(nonFAQQueryRepo.findOne(question.getId()) != null);

		question.setAnswer("the answer is spaghet");

		boolean status = lineMessenger.respondToQuery(heng.getLineId(), question.getQuery(), question.getAnswer());
		assertTrue(status);
	}

}
