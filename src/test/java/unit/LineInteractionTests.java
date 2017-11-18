package unit;

import comp3111.Application;
import comp3111.LineMessenger;
import comp3111.ScheduledTasks;
import comp3111.Utils;
import comp3111.data.DBManager;
import comp3111.data.model.*;
import comp3111.data.repo.*;
import comp3111.input.exceptions.OfferingDateUnsupportedException;
import comp3111.input.exceptions.OfferingOutOfRoomException;
import comp3111.input.exceptions.TourGuideUnavailableException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
	private BookingRepository bookingRepo;
	@Autowired
	private OfferingRepository offeringRepo;
	@Autowired
	private LineMessenger lineMessenger;
	@Autowired
	private ScheduledTasks sTask;
	@Autowired
	private DBManager actionManager;

	Customer heng, kv;
	static final String HENG_CHATBOT_TO_HENG = "U6934790c40beeed33b8b89fa359aa9cf";
	static final String KV_CHATBOT_TO_HENG = "Uc8f613f85e41d93ed9ffa228188466d1";
	static final String HENG_CHATBOT_TO_KV = "Ufbae1ebc457e163d0f351c1865daccf5";
	static final String KV_CHATBOT_TO_KV = "U7e5b42b4ea64a1ff1d812a3ff33b48b0";

	@Before
	public void setUp() {

		heng = new Customer("heng", KV_CHATBOT_TO_HENG, "12312341234", 20, "A1234563");
		heng = customerRepo.save(heng);
		kv = new Customer("KV", KV_CHATBOT_TO_KV, "12312341234", 20, "A1234563");
		kv = customerRepo.save(kv);
	}

	@After
	public void tearDown() {
		actionManager.deleteAll();
		LineMessenger.resetCounter();
	}

	@Test
	public void testSuccessSendMessageToHengsPhone() {

		boolean recieved200ok = lineMessenger.sendToUser(heng.getLineId(), "hi heng's phone, this is test case", true);
		assertTrue(recieved200ok);
		assertEquals(1, LineMessenger.getCounter());
	}

	@Test
	public void testSuccessSendMessageToKVsPhone() {

		boolean recieved200ok = lineMessenger.sendToUser(kv.getLineId(), "hi kv's phone, this is test case", true);
		assertTrue(recieved200ok);
		assertEquals(1, LineMessenger.getCounter());
	}

	@Test
	public void testSuccessSendMessageToOfferingParticipants() throws OfferingDateUnsupportedException,
			TourGuideUnavailableException, OfferingOutOfRoomException {

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
				Booking.PAYMENT_PENDING,1);
		Booking kvBooking = actionManager.createBookingForOffering(offering, kv, 3, 4, 2, 0, "kids meal",
				Booking.PAYMENT_PENDING,1);

		boolean status = lineMessenger.sendToOffering(offering, "send to all participants in offering test case");
		assertTrue(status);
		assertEquals(2, LineMessenger.getCounter()); // should have two participants
	}

	@Test
	public void testSuccessCanPersistNonFAQQueryAndRespond() {

		NonFAQQuery question = new NonFAQQuery("what is spaghet", heng);
		question = nonFAQQueryRepo.save(question);
		assertTrue(nonFAQQueryRepo.findOne(question.getId()) != null);

		question.setAnswer("the answer is spaghet");

		boolean status = lineMessenger.respondToQuery(heng.getLineId(), question.getQuery(), question.getAnswer());
		assertTrue(status);
	}

	@Test
	public void testSuccessManuallyCancelOffering()
			throws OfferingDateUnsupportedException, TourGuideUnavailableException, OfferingOutOfRoomException {

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
				Booking.PAYMENT_PENDING,1);
		Booking kvBooking = actionManager.createBookingForOffering(offering, kv, 3, 4, 2, 0, "kids meal",
				Booking.PAYMENT_PENDING,1);

		LineMessenger.resetCounter();
		actionManager.notifyOfferingStatus(offering, false);
		assertEquals(2, LineMessenger.getCounter()); // should have two "200 OK"s from LINE

		// need to refetch the items from DB

		assertEquals(bookingRepo.findOne(hengBooking.getId()).getPaymentStatus(),
				Booking.PAYMENT_CANCELLED_BECAUSE_OFFERING_CANCELLED);
		assertEquals(bookingRepo.findOne(kvBooking.getId()).getPaymentStatus(),
				Booking.PAYMENT_CANCELLED_BECAUSE_OFFERING_CANCELLED);

		assertEquals(offeringRepo.findOne(offering.getId()).getStatus(), Offering.STATUS_CANCELLED);
	}

	@Test
	public void testSuccessOfferingAutomaticConfirmedCapacity()
			throws OfferingDateUnsupportedException, TourGuideUnavailableException, OfferingOutOfRoomException {
		Date now = new Date();
		Date twoDaysAgo = Utils.addDate(now, -2); // so that our trigger will act on this tour

		Tour comp3111Tour = new Tour("comp3111h", "learn about design patterns", 3, 0.8, 0.0, 599, 699);
		comp3111Tour.setAllowedDates(new HashSet<Date>(Arrays.asList(twoDaysAgo)));
		comp3111Tour = tourRepo.save(comp3111Tour);

		TourGuide tg1 = new TourGuide("professor kim", "line123");
		tg1 = tourGuideRepo.save(tg1);

		int minCapacity = 5;
		Offering offering = actionManager.createOfferingForTour(comp3111Tour, tg1, twoDaysAgo, "hotel bob", minCapacity,
				20);

		Booking hengBooking = actionManager.createBookingForOffering(offering, heng, 3, 4, 2, 0, "kids meal",
				Booking.PAYMENT_CONFIRMED,1);
		Booking kvBooking = actionManager.createBookingForOffering(offering, kv, 3, 4, 2, 0, "kids meal",
				Booking.PAYMENT_CONFIRMED,1);

		// this should be enough customers to trigger a success
		sTask.updatePendingOfferingStatusIfNecessary();

		assertEquals(2, LineMessenger.getCounter());
		// should have two "200 OK"s from LINE. we get it immediately after the schedule
		// task, without resetting the counter.

		// need to refetch the items from DB
		assertEquals(Booking.PAYMENT_CONFIRMED, bookingRepo.findOne(hengBooking.getId()).getPaymentStatus());
		assertEquals(Booking.PAYMENT_CONFIRMED, bookingRepo.findOne(kvBooking.getId()).getPaymentStatus());
		assertEquals(Offering.STATUS_CONFIRMED, offeringRepo.findOne(offering.getId()).getStatus());
	}

	@Test
	public void testFailureOfferingAutomaticCancelNotEnoughConfirmedPayments()
			throws OfferingDateUnsupportedException, TourGuideUnavailableException, OfferingOutOfRoomException {
		Date now = new Date();
		Date twoDaysAgo = Utils.addDate(now, -2); // so that our trigger will act on this tour

		Tour comp3111Tour = new Tour("comp3111h", "learn about design patterns", 3, 0.8, 0.0, 599, 699);
		comp3111Tour.setAllowedDates(new HashSet<Date>(Arrays.asList(twoDaysAgo)));
		comp3111Tour = tourRepo.save(comp3111Tour);

		TourGuide tg1 = new TourGuide("professor kim", "line123");
		tg1 = tourGuideRepo.save(tg1);

		int minCapacity = 5;
		Offering offering = actionManager.createOfferingForTour(comp3111Tour, tg1, twoDaysAgo, "hotel bob", minCapacity,
				20);

		Booking hengBooking = actionManager.createBookingForOffering(offering, heng, 3, 4, 2, 0, "kids meal",
				Booking.PAYMENT_PENDING,1);
		Booking kvBooking = actionManager.createBookingForOffering(offering, kv, 3, 4, 2, 0, "kids meal",
				Booking.PAYMENT_PENDING,1);

		// this should be enough customers to trigger a success
		sTask.updatePendingOfferingStatusIfNecessary();

		assertEquals(2, LineMessenger.getCounter());
		// should have two "200 OK"s from LINE. we get it immediately after the schedule
		// task, without resetting the counter.

		// need to refetch the items from DB
		assertEquals(Booking.PAYMENT_CANCELLED_BECAUSE_OFFERING_CANCELLED,
				bookingRepo.findOne(hengBooking.getId()).getPaymentStatus());
		assertEquals(Booking.PAYMENT_CANCELLED_BECAUSE_OFFERING_CANCELLED,
				bookingRepo.findOne(kvBooking.getId()).getPaymentStatus());
		assertEquals(Offering.STATUS_CANCELLED, offeringRepo.findOne(offering.getId()).getStatus());
	}

	@Test
	public void testSuccessAutomatedCheckingAt8AMEveryDay() {
		// to test if a cron expression runs every day at 8 AM
		CronTrigger trigger = new CronTrigger(ScheduledTasks.EVERYDAY_8_AM);
		Calendar today = Calendar.getInstance();
		today.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);

		final Date yesterday = today.getTime();
		Date nextExecutionTime = trigger.nextExecutionTime(new TriggerContext() {

			@Override
			public Date lastScheduledExecutionTime() {
				return yesterday;
			}

			@Override
			public Date lastActualExecutionTime() {
				return yesterday;
			}

			@Override
			public Date lastCompletionTime() {
				return yesterday;
			}
		});

		assertEquals(nextExecutionTime.getHours(), 8);
		assertTrue(nextExecutionTime.before(Utils.addDate(yesterday, 1)));
	}

}
