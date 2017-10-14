package comp3111;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import comp3111.exceptions.OfferingDateUnsupportedException;
import comp3111.exceptions.OfferingDayOfWeekUnsupportedException;
import comp3111.exceptions.OfferingOutOfRoomException;
import comp3111.exceptions.TourGuideUnavailableException;
import comp3111.exceptions.UsernameTakenException;
import comp3111.model.Customer;
import comp3111.model.CustomerOffering;
import comp3111.model.LoginUser;
import comp3111.model.Offering;
import comp3111.model.Tour;
import comp3111.model.TourGuide;
import comp3111.repo.CustomerOfferingRepository;
import comp3111.repo.CustomerRepository;
import comp3111.repo.LoginUserRepository;
import comp3111.repo.OfferingRepository;
import comp3111.repo.TourGuideRepository;
import comp3111.repo.TourRepository;
import comp3111.validators.Utils;

@Component
public class ActionManager {
	private static final Logger log = LoggerFactory.getLogger(ActionManager.class);

	@Autowired
	private CustomerRepository customerRepo;
	@Autowired
	private CustomerOfferingRepository customerOfferingRepo;
	@Autowired
	private TourRepository tourRepo;
	@Autowired
	private LoginUserRepository loginUserRepo;
	@Autowired
	private OfferingRepository offeringRepo;
	@Autowired
	private TourGuideRepository tourGuideRepo;

	/*
	 * will save or update the tour and tourguide and set up the connections
	 */
	public Offering createOfferingForTour(Tour tour, TourGuide tg, Date startDate, String hotelName, int minCustomers,
			int maxCustomers) throws OfferingOutOfRoomException, OfferingDateUnsupportedException,
			OfferingDayOfWeekUnsupportedException, TourGuideUnavailableException {

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
		for (Offering of : tg.getGuidedOfferings()) {
			Date occupiedStartDate = of.getStartDate();
			Date occupiedEndDate = this.addDate(occupiedStartDate, of.getTour().getDays());
			if (startDate.after(occupiedStartDate) && startDate.before(occupiedEndDate)) {
				throw new TourGuideUnavailableException();
			}

		}

		Offering o = offeringRepo.save(new Offering(tour, tg, startDate, hotelName, minCustomers, maxCustomers));

		tour.addOffering(o);
		tg.getGuidedOfferings().add(o);
		o.setTour(tour);
		o.setTourGuide(tg);

		tourRepo.save(tour);
		tourGuideRepo.save(tg);

		log.info("successfully created offering on [{}] for tour [{}]", startDate, tour.getTourName());
		return o;

	}

	//
	// /*
	// * will save or update the offering and customer into db
	// */
	public CustomerOffering createBookingForOffering(Offering o, Customer c, int numAdults, int numChildren,
			int numToddlers, double amountPaid, String specialRequests, String paymentStatus)
			throws OfferingOutOfRoomException {
		int totalWantToJoin = numAdults + numChildren + numToddlers;
		int spotsTaken = 0;
		for (CustomerOffering rec : o.getCustomerOffering()) {
			spotsTaken += rec.getNumAdults() + rec.getNumChildren() + rec.getNumToddlers();
		}
		int spotsLeft = o.getMaxCustomers() - spotsTaken;
		if (totalWantToJoin > spotsLeft) {
			throw new OfferingOutOfRoomException();
		}

		// save the customerOffering first, which will automatically save offering and
		// customer (IMPORTANT)
		CustomerOffering bookingRecord = customerOfferingRepo.save(new CustomerOffering(c, o, numAdults, numChildren,
				numToddlers, amountPaid, specialRequests, paymentStatus));
		o.getCustomerOffering().add(bookingRecord);
		c.getCustomerOffering().add(bookingRecord);

		// DON'T do this: or else multiple records saved
		// offeringRepo.save(o);
		// customerRepo.save(c);
		log.info("customer [{}] succesfully made a booking for tour [{}], offering [{}]", c.getName(),
				o.getTour().getTourName(), o.getStartDate());

		return bookingRecord;

	}

	public void deleteAll() {
		this.customerOfferingRepo.deleteAll();
		this.customerRepo.deleteAll();
		this.tourRepo.deleteAll();
		this.offeringRepo.deleteAll();
		this.tourGuideRepo.deleteAll();
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

	private Date addDate(Date d, int days) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}
}
