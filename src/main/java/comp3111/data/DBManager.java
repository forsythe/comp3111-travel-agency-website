package comp3111.data;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import comp3111.Utils;
import comp3111.data.model.Booking;
import comp3111.data.model.Customer;
import comp3111.data.model.LoginUser;
import comp3111.data.model.Offering;
import comp3111.data.model.Tour;
import comp3111.data.model.TourGuide;
import comp3111.data.repo.BookingRepository;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.LoginUserRepository;
import comp3111.data.repo.OfferingRepository;
import comp3111.data.repo.TourGuideRepository;
import comp3111.data.repo.TourRepository;
import comp3111.exceptions.OfferingDateUnsupportedException;
import comp3111.exceptions.OfferingDayOfWeekUnsupportedException;
import comp3111.exceptions.OfferingOutOfRoomException;
import comp3111.exceptions.TourGuideUnavailableException;
import comp3111.exceptions.UsernameTakenException;

@Component
public class DBManager {
	private static final Logger log = LoggerFactory.getLogger(DBManager.class);

	@Autowired
	private CustomerRepository customerRepo;
	@Autowired
	private BookingRepository bookingRepo;
	@Autowired
	private TourRepository tourRepo;
	@Autowired
	private LoginUserRepository loginUserRepo;
	@Autowired
	private OfferingRepository offeringRepo;
	@Autowired
	private TourGuideRepository tourGuideRepo;

	public Offering createOfferingForTour(Tour tour, TourGuide tg, Date startDate, String hotelName, int minCustomers,
			int maxCustomers) throws OfferingDateUnsupportedException, OfferingDayOfWeekUnsupportedException,
			TourGuideUnavailableException {

		tourRepo.save(tour);
		tourGuideRepo.save(tg);

		// check that the day of week is correct
		Collection<Integer> supportedDaysOfWeek = tour.getAllowedDaysOfWeek();
		if (!supportedDaysOfWeek.isEmpty()) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int startDateDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
			// Sunday: 1, Monday: 2, Tuesday: 3...
			log.info("For offering for tour[{}], proposed startDate = [{}]", tour.getTourName(),
					Utils.dayToString(startDateDayOfWeek));
			if (!supportedDaysOfWeek.contains(startDateDayOfWeek)) {
				log.error("Error! only the following days are supported");
				for (Integer i : supportedDaysOfWeek) {
					log.error("\t[{}]", Utils.dayToString(i));
				}

				throw new OfferingDayOfWeekUnsupportedException();
			}
		}
		Collection<Date> supportedDates = tour.getAllowedDates();
		log.info("For offering for tour[{}], proposed startDate is [{}]", tour.getTourName(), startDate);
		if (!supportedDates.isEmpty() && !supportedDates.contains(startDate)) {
			log.error("Error! only the following dates are supported");
			for (Date d : supportedDates) {
				log.error("\t[{}]", d);
			}
			throw new OfferingDateUnsupportedException();
		}

		for (Offering of : findGuidedOfferingsByTourGuide(tg)) {
			Date occupiedStartDate = of.getStartDate();
			Date occupiedEndDate = Utils.addDate(occupiedStartDate, of.getTour().getDays());
			if (startDate.after(occupiedStartDate) && startDate.before(occupiedEndDate)) {
				throw new TourGuideUnavailableException();
			}

		}

		Offering o = offeringRepo.save(new Offering(tour, tg, startDate, hotelName, minCustomers, maxCustomers));

		o.setTour(tour);
		o.setTourGuide(tg);

		offeringRepo.save(o);

		log.info("successfully created offering on [{}] for tour [{}]", startDate, tour.getTourName());
		return o;
	}

	public void createOfferingForTour(Offering offering) throws OfferingDateUnsupportedException,
			OfferingDayOfWeekUnsupportedException, TourGuideUnavailableException {
		createOfferingForTour(offering.getTour(), offering.getTourGuide(), offering.getStartDate(),
				offering.getHotelName(), offering.getMinCustomers(), offering.getMaxCustomers());
	}

	public Booking createBookingForOffering(Offering o, Customer c, int numAdults, int numChildren, int numToddlers,
			double amountPaid, String specialRequests, String paymentStatus) throws OfferingOutOfRoomException {
		int totalWantToJoin = numAdults + numChildren + numToddlers;
		int spotsTaken = 0;

		o = offeringRepo.save(o);
		c = customerRepo.save(c);

		for (Booking rec : findBookingsForOffering(o)) {
			spotsTaken += rec.getNumAdults() + rec.getNumChildren() + rec.getNumToddlers();
		}
		int spotsLeft = o.getMaxCustomers() - spotsTaken;
		if (totalWantToJoin > spotsLeft) {
			throw new OfferingOutOfRoomException();
		}

		Booking bookingRecord = bookingRepo.save(
				new Booking(c, o, numAdults, numChildren, numToddlers, amountPaid, specialRequests, paymentStatus));

		log.info("customer [{}] succesfully made a booking for tour [{}], offering [{}]", c.getName(),
				o.getTour().getTourName(), o.getStartDate());

		return bookingRecord;

	}

	public void createBookingForOffering(Booking booking) throws OfferingOutOfRoomException {
		createBookingForOffering(booking.getOffering(), booking.getCustomer(), booking.getNumAdults(),
				booking.getNumChildren(), booking.getNumToddlers(), booking.getAmountPaid(),
				booking.getSpecialRequests(), booking.getPaymentStatus());
	}

	public Collection<Offering> findGuidedOfferingsByTourGuide(TourGuide tg) {
		Collection<Offering> guidedOfferingsByTourGuide = new HashSet<Offering>();
		for (Offering o : offeringRepo.findAll()) {
			if (o.getTourGuide().equals(tg)) {
				guidedOfferingsByTourGuide.add(o);
			}
		}
		return guidedOfferingsByTourGuide;
	}

	public Collection<Booking> findBookingsForOffering(Offering offering) {
		Collection<Booking> bookingsForOffering = new HashSet<Booking>();
		for (Booking record : bookingRepo.findAll()) {
			if (record.getOffering().equals(offering)) {
				bookingsForOffering.add(record);
			}
		}
		return bookingsForOffering;
	}

	public void deleteAll() {
		this.bookingRepo.deleteAll();
		this.offeringRepo.deleteAll();
		this.tourRepo.deleteAll();
		this.tourGuideRepo.deleteAll();
		this.customerRepo.deleteAll();

		log.info("successfully cleared all repos");
		// this.loginUserRepo.deleteAll();
	}

	public void createNewLogin(String username, String rawPassword) throws UsernameTakenException {
		if (loginUserRepo.findByUsername(username) != null) {
			throw new UsernameTakenException();
		}
		loginUserRepo.save(new LoginUser(username, rawPassword));
		log.info("successfully created a new user login, username [{}]", username);
	}

	public int countNumOfferingsForTour(Tour t) {
		int count = 0;
		for (Booking o : bookingRepo.findAll()) {
			if (o.getTourId().equals(t.getId()))
				count++;
		}
		return count;
	}
}
