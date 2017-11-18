package comp3111.data;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import comp3111.input.exceptions.*;
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
import comp3111.input.validators.DateAvailableInTourValidator;
import comp3111.input.validators.ValidatorFactory;

/**
 * A mediator class responsible for handling common database actions.
 * 
 * @author Forsythe
 *
 */
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
	private PromoEventRepository promoEventRepo;
	@Autowired
	private LineMessenger lineMessenger;

	/**
	 * Creates and persists a new offering in the database.
	 * 
	 * @param tour
	 *            The tour for which the offering belongs to
	 * @param tg
	 *            The tour guide leading this offering
	 * @param startDate
	 *            When the offering starts
	 * @param hotelName
	 *            The hotel name
	 * @param minCustomers
	 *            The minimum number of customers required for this offering to be
	 *            considered as {@link Offering#STATUS_CONFIRMED}
	 * @param maxCustomers
	 *            The maximum number of spots available for this offering
	 * @return The persisted offering object
	 * @throws OfferingDateUnsupportedException
	 *             If you try to create an offering on a date or day of week for
	 *             which the Tour does not support
	 * @throws TourGuideUnavailableException
	 *             If you try to assign a tour guide who has a time conflict with
	 *             this offering
	 */
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

	/**
	 * @param o
	 *            The updated transient Offering object
	 * @return The persisted Offering object
	 * @throws TourGuideUnavailableException
	 *             If you try to assign a tour guide who has a time conflict with
	 *             this offering
	 * @throws OfferingDateUnsupportedException
	 *             If you try to create an offering on a date or day of week for
	 *             which the Tour does not support
	 */
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

	/**
	 * @param tg
	 *            The tour guide
	 * @param proposedOffering
	 *            An offering
	 * @return Whether or not the tour guide is free to lead this offering.
	 */
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
	 * @return Whether the tour guide is available between the specified range. It
	 *         scans all offerings associated with this tour guide, and checks if
	 *         there are any time collisions with the provided date range. We also
	 *         ignore the timerange used by the offering "ignoredOffering"
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

	/**
	 * Persists a transient offering object into the database
	 * 
	 * @param offering
	 *            A transient offering object
	 * @throws TourGuideUnavailableException
	 *             If you try to assign a tour guide who has a time conflict with
	 *             this offering
	 * @throws OfferingDateUnsupportedException
	 *             If you try to create an offering on a date or day of week for
	 *             which the Tour does not support
	 */
	public void createOfferingForTour(Offering offering)
			throws OfferingDateUnsupportedException, TourGuideUnavailableException {
		createOfferingForTour(offering.getTour(), offering.getTourGuide(), offering.getStartDate(),
				offering.getHotelName(), offering.getMinCustomers(), offering.getMaxCustomers());
	}

	/**
	 * @param o
	 *            An offering to make a booking in
	 * @param c
	 *            Customer who made the booking
	 * @param numAdults
	 *            Number of adults
	 * @param numChildren
	 *            Number of children
	 * @param numToddlers
	 *            Number of toddlers
	 * @param amountPaid
	 *            The dollar amount the customer has already paid
	 * @param specialRequests
	 *            Any special requests made by the customer
	 * @param paymentStatus
	 *            The payment status. Can be {@link Booking#PAYMENT_PENDING},
	 *            {@link Booking#PAYMENT_CONFIRMED}, or
	 *            {@link Booking#PAYMENT_CANCELLED_BECAUSE_OFFERING_CANCELLED}
	 * @param discount
	 *            A double between [0, 1.0] indicating the discount multiplier. 1.0
	 *            for full price (no discount).
	 * @param promoCodeUsed
	 *            The promo code used
	 * @return The persisted Booking object
	 * @throws OfferingOutOfRoomException
	 *             If, after adding the number of adults, children, and toddlers to
	 *             the current number of people in this offering, the value exceeds
	 *             {@link Offering#getMaxCustomers()}
	 */
	public Booking createBookingForOffering(Offering o, Customer c, int numAdults, int numChildren, int numToddlers,
			double amountPaid, String specialRequests, String paymentStatus, double discount, String promoCodeUsed)
			throws OfferingOutOfRoomException {
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

		Booking bookingRecord = bookingRepo.save(new Booking(c, o, numAdults, numChildren, numToddlers, amountPaid,
				specialRequests, paymentStatus, discount, promoCodeUsed));

		log.info("customer [{}] succesfully made a booking for tour [{}], offering [{}]", c.getName(),
				o.getTour().getTourName(), o.getStartDate());

		return bookingRecord;
	}

	/**
	 * @param o
	 *            An offering to make a booking in
	 * @param c
	 *            Customer who made the booking
	 * @param numAdults
	 *            Number of adults
	 * @param numChildren
	 *            Number of children
	 * @param numToddlers
	 *            Number of toddlers
	 * @param amountPaid
	 *            The dollar amount the customer has already paid
	 * @param specialRequests
	 *            Any special requests made by the customer
	 * @param paymentStatus
	 *            The payment status. Can be {@link Booking#PAYMENT_PENDING},
	 *            {@link Booking#PAYMENT_CONFIRMED}, or
	 *            {@link Booking#PAYMENT_CANCELLED_BECAUSE_OFFERING_CANCELLED}
	 * @param discount
	 *            A double between [0, 1.0] indicating the discount multiplier. 1.0
	 *            for full price (no discount).
	 * @return The persisted Booking object
	 * @throws OfferingOutOfRoomException
	 *             If, after adding the number of adults, children, and toddlers to
	 *             the current number of people in this offering, the value exceeds
	 *             {@link Offering#getMaxCustomers()}
	 */
	public Booking createBookingForOffering(Offering o, Customer c, int numAdults, int numChildren, int numToddlers,
			double amountPaid, String specialRequests, String paymentStatus, double discount)
			throws OfferingOutOfRoomException {
		return createBookingForOffering(o, c, numAdults, numChildren, numToddlers, amountPaid, specialRequests,
				paymentStatus, discount, null);
	}

	/**
	 * Persists a transient booking object
	 * 
	 * @param booking
	 *            A transient Booking object
	 * @throws OfferingOutOfRoomException
	 *             If, after adding the number of adults, children, and toddlers to
	 *             the current number of people in this offering, the value exceeds
	 *             {@link Offering#getMaxCustomers()}
	 */
	public void createNormalBookingForOffering(Booking booking) throws OfferingOutOfRoomException {
		createBookingForOffering(booking.getOffering(), booking.getCustomer(), booking.getNumAdults(),
				booking.getNumChildren(), booking.getNumToddlers(), booking.getAmountPaid(),
				booking.getSpecialRequests(), booking.getPaymentStatus(), 1, null);
	}

	/**
	 * @param promoCode
	 *            The promo code
	 * @return Whether this promo code is valid
	 * @throws PromoCodeUsedUpException
	 *             If the promo code has been used up
	 * @throws NoSuchPromoCodeException
	 *             If the promo code doesn't exist
	 */
	private boolean validatePromoCode(String promoCode) throws PromoCodeUsedUpException, NoSuchPromoCodeException {
		PromoEvent pe = promoEventRepo.findOneByPromoCode(promoCode);

		if (pe == null)
			throw new NoSuchPromoCodeException();
		if (pe.getPromoCodeUsesLeft() == 0)
			throw new PromoCodeUsedUpException();
		return true;
	}

	/**
	 * @param booking
	 *            The booking to make an offering for
	 * @param promocode
	 *            The promo code
	 * @throws OfferingOutOfRoomException
	 * @throws PromoCodeUsedUpException
	 * @throws NoSuchPromoCodeException
	 * @throws PromoForCustomerExceededException
	 * @throws PromoCodeNotForOfferingException
	 */
	public void createBookingForOfferingWithPromoCode(Booking booking, String promocode)
			throws OfferingOutOfRoomException, PromoCodeUsedUpException, NoSuchPromoCodeException,
			PromoForCustomerExceededException, PromoCodeNotForOfferingException {
		validatePromoCode(promocode);

		PromoEvent pe = promoEventRepo.findOneByPromoCode(promocode);
		if (!pe.getOffering().equals(booking.getOffering())) {
			throw new PromoCodeNotForOfferingException();
		}

		int totalPromoUsed = booking.getTotalNumberOfPeople();
		for (Booking b : bookingRepo.findByCustomer(booking.getCustomer())) {
			if (b.getPromoCodeUsed() != null && b.getPromoCodeUsed().equals(promocode)) {
				totalPromoUsed += b.getTotalNumberOfPeople();
			}
		}
		if (totalPromoUsed > pe.getMaxReservationsPerCustomer()) {
			throw new PromoForCustomerExceededException();
		}

		createBookingForOffering(booking.getOffering(), booking.getCustomer(), booking.getNumAdults(),
				booking.getNumChildren(), booking.getNumToddlers(), booking.getAmountPaid(),
				booking.getSpecialRequests(), booking.getPaymentStatus(), pe.getDiscount(), pe.getPromoCode());

		pe.setPromoCodeUsesLeft(pe.getPromoCodeUsesLeft() - 1);
		promoEventRepo.save(pe);
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

	/**
	 * @param offering
	 *            An offering
	 * @return All bookings made for this offering
	 */
	public Collection<Booking> findBookingsForOffering(Offering offering) {
		Collection<Booking> bookingsForOffering = new HashSet<Booking>();
		for (Booking record : bookingRepo.findAll()) {
			if (record.getOffering().equals(offering)) {
				bookingsForOffering.add(record);
			}
		}
		return bookingsForOffering;
	}

	/**
	 * Clears out all the repositories (except the login user one)
	 */
	public void deleteAll() {
		this.promoEventRepo.deleteAll();
		this.bookingRepo.deleteAll();
		this.offeringRepo.deleteAll();
		this.tourRepo.deleteAll();
		this.tourGuideRepo.deleteAll();
		this.nonFAQQueryRepo.deleteAll();
		this.customerRepo.deleteAll();
		this.promoEventRepo.deleteAll();

		log.info("successfully cleared all repos");
		// this.loginUserRepo.deleteAll();
	}

	/**
	 * Create a new login user
	 * 
	 * @param username
	 *            The username
	 * @param rawPassword
	 *            The raw password
	 * @throws UsernameTakenException
	 *             If the username is taken
	 */
	public void createNewLogin(String username, String rawPassword) throws UsernameTakenException {
		if (loginUserRepo.findByUsername(username) != null) {
			throw new UsernameTakenException();
		}
		loginUserRepo.save(new LoginUser(username, rawPassword));
		log.info("successfully created a new user login, username [{}]", username);
	}

	/**
	 * @param t
	 *            A tour
	 * @return How many offerings have been made for this tour
	 */
	public int countNumOfferingsForTour(Tour t) {
		int count = 0;
		for (Offering o : offeringRepo.findAll()) {
			if (o.getTour().equals(t))
				count++;
		}
		return count;
	}

	/**
	 * @param offering
	 *            An offering
	 * @return How many paid customers (adults, children, toddlers) are registered
	 *         for this offering
	 */
	public int countNumberOfPaidPeopleInOffering(Offering offering) {
		int num = 0;
		for (Booking record : bookingRepo.findByOffering(offering)) {
			if (record.getPaymentStatus().equals(Booking.PAYMENT_CONFIRMED)) {
				num += record.getNumAdults() + record.getNumChildren() + record.getNumToddlers();
			}
		}
		return num;
	}

	/**
	 * Notifies all customers via LINE who have made bookings for the specified
	 * offering about the status of said offering.
	 * 
	 * It updates the offering object's status in the database (
	 * {@link Offering#STATUS_CANCELLED} or {@link Offering#STATUS_CONFIRMED} ).
	 * 
	 * If confirmed, a success message is sent to all associated customers.
	 * 
	 * However, if the offering is cancelled, then all booking records associated
	 * with this offering are set to
	 * {@link Booking#PAYMENT_CANCELLED_BECAUSE_OFFERING_CANCELLED}
	 * 
	 * @param offering
	 *            An offering
	 * @param confirmed
	 *            The new status of the offering
	 * 
	 */
	public void notifyOfferingStatus(Offering offering, boolean confirmed) {
		log.info("Setting [{}] to be [{}]", offering, confirmed ? "confirmed" : "cancelled");
		offering.setStatus(confirmed ? Offering.STATUS_CONFIRMED : Offering.STATUS_CANCELLED);
		offering = offeringRepo.save(offering);
		if (!confirmed) {
			for (Booking record : bookingRepo.findByOffering(offering)) {
				record.setPaymentStatus(Booking.PAYMENT_CANCELLED_BECAUSE_OFFERING_CANCELLED);
				record = bookingRepo.save(record);
				log.info("Cancelling booking record [{}]", record);
			}
			lineMessenger.sendToOffering(offering, offering + " has been cancelled.");
		} else {

			lineMessenger.sendToOffering(offering, offering + " has been confirmed! See you soon!");
		}

	}

}
