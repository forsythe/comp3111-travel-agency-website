package comp3111;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import comp3111.exceptions.OfferingDateUnsupportedException;
import comp3111.exceptions.OfferingOutOfRoomException;
import comp3111.exceptions.UsernameTakenException;
import comp3111.model.Customer;
import comp3111.model.CustomerOffering;
import comp3111.model.LimitedTour;
import comp3111.model.LoginUser;
import comp3111.model.Offering;
import comp3111.model.RepeatingTour;
import comp3111.model.Tour;
import comp3111.model.TourGuide;
import comp3111.repo.CustomerOfferingRepository;
import comp3111.repo.CustomerRepository;
import comp3111.repo.LimitedTourRepository;
import comp3111.repo.LoginUserRepository;
import comp3111.repo.OfferingRepository;
import comp3111.repo.RepeatingTourRepository;
import comp3111.repo.TourGuideRepository;

@Component
public class ActionManager {
	private static final Logger log = LoggerFactory.getLogger(ActionManager.class);

	@Autowired
	private CustomerRepository customerRepo;
	@Autowired
	private CustomerOfferingRepository customerOfferingRepo;
	@Autowired
	private LimitedTourRepository limitedTourRepo;
	@Autowired
	private LoginUserRepository loginUserRepo;
	@Autowired
	private OfferingRepository offeringRepo;
	@Autowired
	private RepeatingTourRepository repeatingTourRepo;
	@Autowired
	private TourGuideRepository tourGuideRepo;

	/*
	 * will save or update the tour and tourguide and set up the connections
	 */
	public Offering createOfferingForTour(Tour tour, TourGuide tg, Date startDate, String hotelName, int minCustomers,
			int maxCustomers)
			throws OfferingOutOfRoomException, UsernameTakenException, OfferingDateUnsupportedException {

		if (tour instanceof RepeatingTour) {
			Collection<Integer> supported = ((RepeatingTour) tour).getDaysOffered();
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int startDateDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
			// Sunday: 1, Monday: 2, Tuesday: 3...

			if (!supported.contains(startDateDayOfWeek)) {
				throw new OfferingOutOfRoomException();
			}
		} else if (tour instanceof LimitedTour) {
			Collection<Date> supported = ((LimitedTour) tour).getDatesOffered();
			if (!supported.contains(startDate)) {
				throw new OfferingDateUnsupportedException();
			}
		}
		for (Offering of : tg.getGuidedOfferings()) {
			Date occupiedStartDate = of.getStartDate();
			Date occupiedEndDate = this.addDate(occupiedStartDate, of.getTour().getDays());
			if (startDate.after(occupiedStartDate) && startDate.before(occupiedEndDate)) {
				throw new UsernameTakenException();
			}

		}

		Offering o = new Offering(tour, tg, startDate, hotelName, minCustomers, maxCustomers);

		tour.addOffering(o);
		o.setTour(tour);
		o.setTourGuide(tg);
		tg.getGuidedOfferings().add(o);

		if (tour instanceof RepeatingTour) {
			repeatingTourRepo.save((RepeatingTour) tour);
		} else if (tour instanceof LimitedTour) {
			limitedTourRepo.save((LimitedTour) tour);
		}
		tourGuideRepo.save(tg);

		log.info("successfully created offering for tour [{}]", tour.getTourName());
		return offeringRepo.save(o);

	}

	//
	// /*
	// * will save or update the offering and customer into db
	// */
	public void createBookingForOffering(Offering o, Customer c, int numAdults, int numChildren, int numToddlers,
			double amountPaid, String specialRequests, String paymentStatus) throws OfferingOutOfRoomException {
		int totalWantToJoin = numAdults + numChildren + numToddlers;
		int spotsTaken = 0;
		for (CustomerOffering rec : o.getCustomerOffering()) {
			spotsTaken += rec.getNumAdults() + rec.getNumChildren() + rec.getNumToddlers();
		}
		int spotsLeft = o.getMaxCustomers() - spotsTaken;
		if (totalWantToJoin > spotsLeft) {
			throw new OfferingOutOfRoomException();
		}

		CustomerOffering bookingRecord = new CustomerOffering(c, o, numAdults, numChildren, numToddlers, amountPaid,
				specialRequests, paymentStatus);
		// save the customerOffering first, which will automatically save offering and
		// customer (IMPORTANT)
		customerOfferingRepo.save(bookingRecord);

		o.getCustomerOffering().add(bookingRecord);
		c.getCustomerOffering().add(bookingRecord);

		//DON'T do this: or else multiple records saved
		// offeringRepo.save(o);
		// customerRepo.save(c);
		log.info("customer [{}] succesfully made a booking for [{}]", c.getName(), o.getTour().getTourName());
	}

	public void deleteAll() {
		this.customerOfferingRepo.deleteAll();
		this.customerRepo.deleteAll();
		this.limitedTourRepo.deleteAll();
		this.offeringRepo.deleteAll();
		this.repeatingTourRepo.deleteAll();
		this.tourGuideRepo.deleteAll();
		log.info("successfully cleared all repos");
		// this.loginUserRepo.deleteAll();
	}

	// public void createNewLogin(String username, String rawPassword) throws
	// UsernameTakenException {
	// if (loginUserRepo.findByUsername(username) != null) {
	// throw new UsernameTakenException();
	// }
	// loginUserRepo.save(new LoginUser(username, rawPassword));
	// log.info("successfully created a new user login");
	// }
	//
	private Date addDate(Date d, int days) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}
}
