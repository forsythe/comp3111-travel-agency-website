package comp3111.data;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.ValueContext;

import comp3111.LineMessenger;
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
import comp3111.data.repo.NonFAQQueryRepository;
import comp3111.data.repo.OfferingRepository;
import comp3111.data.repo.TourGuideRepository;
import comp3111.data.repo.TourRepository;
import comp3111.input.exceptions.OfferingDateUnsupportedException;
import comp3111.input.exceptions.OfferingOutOfRoomException;
import comp3111.input.exceptions.TourGuideUnavailableException;
import comp3111.input.exceptions.UsernameTakenException;
import comp3111.input.validators.DateAvailableInTourValidator;
import comp3111.input.validators.ValidatorFactory;

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
	@Autowired
	private NonFAQQueryRepository nonFAQQueryRepo;
	@Autowired
	private LineMessenger lineMessenger;

	public Offering createOfferingForTour(Tour tour, TourGuide tg, Date startDate, String hotelName, int minCustomers,
			int maxCustomers) throws OfferingDateUnsupportedException, TourGuideUnavailableException {

		// Make sure both the tour and the tour guide are concrete entity in the
		// database
		tour = tourRepo.save(tour);
		tg = tourGuideRepo.save(tg);

		DateAvailableInTourValidator dateValidator = ValidatorFactory.getDateAvailableInTourValidator(tour);
		ValidationResult result = dateValidator.apply(startDate, new ValueContext());
		if (result.isError()) {
			throw new OfferingDateUnsupportedException();
		}

		if (!isTourGuideAvailableBetweenDate(tg, startDate, Utils.addDate(startDate, tour.getDays()))) {
			throw new TourGuideUnavailableException();
		}

		Offering o = offeringRepo.save(
				new Offering(tour, tg, startDate, hotelName, minCustomers, maxCustomers, Offering.STATUS_PENDING));

		o.setTour(tour);
		o.setTourGuide(tg);

		offeringRepo.save(o);

		log.info("successfully created offering on [{}] for tour [{}]", startDate, tour.getTourName());
		return o;
	}

	public Offering editOfferingTorTour(Offering o)
			throws TourGuideUnavailableException, OfferingDateUnsupportedException {
		Tour t = o.getTour();
		Date startDate = o.getStartDate();
		TourGuide tg = o.getTourGuide();

		DateAvailableInTourValidator dateValidator = ValidatorFactory.getDateAvailableInTourValidator(t);
		ValidationResult result = dateValidator.apply(startDate, new ValueContext());
		if (result.isError()) {
			throw new OfferingDateUnsupportedException();
		}

		if (!isTourGuideAvailableBetweenDateExcludeOffering(tg, startDate, Utils.addDate(startDate, t.getDays()), o)) {
			throw new TourGuideUnavailableException();
		}

		log.info("successfully edited offering on [{}] for tour [{}]", startDate, t.getTourName());
		return offeringRepo.save(o);
	}

	public boolean isTourGuideAvailableForOffering(TourGuide tg, Offering proposedOffering) {
		Date proposedStart = proposedOffering.getStartDate();
		Date proposedEnd = Utils.addDate(proposedOffering.getStartDate(), proposedOffering.getTour().getDays());
		return isTourGuideAvailableBetweenDate(tg, proposedStart, proposedEnd);
	}

	/**
	 * @param tg
	 *            The tour guide
	 * @param proposedStart
	 *            The beginning date
	 * @param proposedEnd
	 *            The ending date
	 * @return Whether or not the tour guide is free (to lead more offerings)
	 *         between this date interval
	 */
	public boolean isTourGuideAvailableBetweenDate(TourGuide tg, Date proposedStart, Date proposedEnd) {
		for (Offering existingOffering : findPastAndUpcomingGuidedOfferingsByTourGuide(tg)) {
			if (existingOffering.getStatus().equals(Offering.STATUS_CANCELLED)) {
				continue;
			}
			Date takenStart = existingOffering.getStartDate();
			Date takenEnd = Utils.addDate(takenStart, existingOffering.getTour().getDays());

			if (proposedStart.after(takenStart) && proposedEnd.before(takenEnd)
					|| proposedStart.before(takenStart) && proposedEnd.after(takenEnd)
					|| proposedStart.before(takenEnd) && proposedEnd.after(takenEnd) || proposedStart.equals(takenStart)
					|| proposedEnd.equals(takenEnd)) {
				log.info("Offering timerange [{}]-[{}] is occupied for tourguide [{}]", proposedStart, proposedEnd,
						tg.getName());

				return false;
			}

		}
		return true;
	}

	/**
	 * @param tg
	 *            The tour guide
	 * @param proposedStart
	 *            The start date interval to check if the tour guide is free
	 * @param proposedEnd
	 *            The end date
	 * @param ignoredOffering
	 *            The offering to ignore.
	 * @return True or false, if the tour guide is indeed available between the
	 *         specified range. It scans all offerings associated with this tour
	 *         guide, and checks if there are any time collisions with the provided
	 *         date range. We also ignore the timerange used by the offering
	 *         "ignoredOffering"
	 */
	public boolean isTourGuideAvailableBetweenDateExcludeOffering(TourGuide tg, Date proposedStart, Date proposedEnd,
			Offering ignoredOffering) {
		Collection<Offering> relevantOfferings = findPastAndUpcomingGuidedOfferingsByTourGuide(tg);
		int size = relevantOfferings.size();
		log.info("size before removing: [{}]", size);
		relevantOfferings.removeIf(offer -> offer.equals(ignoredOffering));
		assert relevantOfferings.size() == size - 1;
		log.info("size after removing: [{}]", relevantOfferings.size());

		for (Offering existingOffering : relevantOfferings) {
			if (existingOffering.getStatus().equals(Offering.STATUS_CANCELLED)) {
				continue;
			}
			Date takenStart = existingOffering.getStartDate();
			Date takenEnd = Utils.addDate(takenStart, existingOffering.getTour().getDays());

			if (proposedStart.after(takenStart) && proposedEnd.before(takenEnd)
					|| proposedStart.before(takenStart) && proposedEnd.after(takenEnd)
					|| proposedStart.before(takenEnd) && proposedEnd.after(takenEnd) || proposedStart.equals(takenStart)
					|| proposedEnd.equals(takenEnd)) {
				log.info("Offering timerange [{}]-[{}] is occupied for tourguide [{}]", proposedStart, proposedEnd,
						tg.getName());
				return false;
			}

		}
		return true;
	}

	public void createOfferingForTour(Offering offering)
			throws OfferingDateUnsupportedException, TourGuideUnavailableException {
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

	/**
	 * @param tg
	 *            A tour guide
	 * @return A collection of offerings (of ALL statuses; pending, confirmed,
	 *         cancelled) led by this tour guide in the past and assigned in the
	 *         future
	 */
	public Collection<Offering> findPastAndUpcomingGuidedOfferingsByTourGuide(TourGuide tg) {
		Collection<Offering> guidedOfferingsByTourGuide = new HashSet<Offering>();
		log.info("Finding offerings for tour guide [{}]", tg.getName());
		for (Offering o : offeringRepo.findAll()) {
			if (o.getTourGuide().equals(tg)) {
				guidedOfferingsByTourGuide.add(o);
				log.info("\t[{}]", o.toString());
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
		this.nonFAQQueryRepo.deleteAll();
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
		for (Offering o : offeringRepo.findAll()) {
			if (o.getTour().equals(t))
				count++;
		}
		return count;
	}

	public Collection<TourGuide> findAvailableTourGuidesForOffering(Offering offering) {
		Collection<TourGuide> ans = new HashSet<TourGuide>();
		for (TourGuide tg : tourGuideRepo.findAll()) {
			if (this.isTourGuideAvailableForOffering(tg, offering)) {
				ans.add(tg);
			}
		}
		return ans;
	}

	public Collection<TourGuide> findAvailableTourGuidesBetweenDates(Date start, Date end) {
		Collection<TourGuide> ans = new HashSet<TourGuide>();
		for (TourGuide tg : tourGuideRepo.findAll()) {
			if (this.isTourGuideAvailableBetweenDate(tg, start, end)) {
				ans.add(tg);
			}
		}
		return ans;
	}

	public int countNumberOfPaidPeopleInOffering(Offering offering) {
		int num = 0;
		for (Booking record : bookingRepo.findByOffering(offering)) {
			if (record.getOffering().equals(offering) && record.getPaymentStatus().equals(Booking.PAYMENT_CONFIRMED)) {
				num += record.getNumAdults() + record.getNumChildren() + record.getNumToddlers();
			}
		}
		return num;
	}

	public int cancelOffering(Offering offering) {
		offering.setStatus(Offering.STATUS_CANCELLED);
		log.info("Cancelling [{}]", offering);
		offering = offeringRepo.save(offering);
		for (Booking record : bookingRepo.findByOffering(offering)) {
			record.setPaymentStatus(Booking.PAYMENT_CANCELLED_BECAUSE_OFFERING_CANCELLED);
			record = bookingRepo.save(record);
			lineMessenger.sendToOffering(offering, offering + " has been cancelled.");
			log.info("Cancelling booking record [{}]", record);
		}
		return lineMessenger.getAndResetCount();
	}
	

}
