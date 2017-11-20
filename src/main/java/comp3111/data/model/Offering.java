package comp3111.data.model;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import comp3111.Utils;

/**
 * Represents an offering entity in the database. One tour may have multiple
 * offerings. Offerings can be limited or repeating, depending on their
 * constraints: {@link Tour#getAllowedDaysOfWeek()} and
 * {@link Tour#getAllowedDates()}.
 * 
 * @author Forsythe
 *
 */
@Entity
public class Offering {

	public static final String STATUS_PENDING = "Pending";
	public static final String STATUS_CANCELLED = "Cancelled";
	public static final String STATUS_CONFIRMED = "Confirmed";

	@Id
	@GeneratedValue
	private Long id;
	@ManyToOne(fetch = FetchType.EAGER) // many offerings to 1 tour
	private Tour tour;
	@ManyToOne(fetch = FetchType.EAGER) // many offerings to 1 tour guide
	private TourGuide tourGuide;

	private Date startDate;
	private String hotelName;
	private int minCustomers;
	private int maxCustomers;

	private String status;

	public Offering() {
		this.status = Offering.STATUS_PENDING;
	}

	/**
	 * Construct a transient offering object.
	 * 
	 * @param tour
	 *            The parent tour
	 * @param tourGuide
	 *            The tour guide who will lead this tour
	 * @param startDate
	 *            The start date of the offering
	 * @param hotelName
	 *            The hotel name
	 * @param minCustomers
	 *            The minimum number of customers required for this offering to be
	 *            considered as {@link Offering#STATUS_CONFIRMED}
	 * @param maxCustomers
	 *            The maximum number of spots available for this offering
	 * @param status
	 *            The status of this offering. Can be
	 *            {@link Offering#STATUS_CONFIRMED} (reached
	 *            {@link Offering#minCustomers} at t=-3 days before start),
	 *            {@link Offering#STATUS_CANCELLED} (underfull at t=-3 days before
	 *            start, or manually cancelled), or {@link Offering#STATUS_PENDING}
	 *            (by default)
	 */
	public Offering(Tour tour, TourGuide tourGuide, Date startDate, String hotelName, int minCustomers,
			int maxCustomers, String status) {
		this.tour = tour;
		this.tourGuide = tourGuide;
		this.startDate = startDate;
		this.hotelName = hotelName;
		this.minCustomers = minCustomers;
		this.maxCustomers = maxCustomers;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Tour getTour() {
		return tour;
	}

	public void setTour(Tour tour) {
		this.tour = tour;
	}

	public TourGuide getTourGuide() {
		return tourGuide;
	}

	public void setTourGuide(TourGuide tourGuide) {
		this.tourGuide = tourGuide;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getHotelName() {
		return hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	public int getMinCustomers() {
		return minCustomers;
	}

	public void setMinCustomers(int minCustomers) {
		this.minCustomers = minCustomers;
	}

	public int getMaxCustomers() {
		return maxCustomers;
	}

	public void setMaxCustomers(int maxCustomers) {
		this.maxCustomers = maxCustomers;
	}

	/**
	 * @return the status of the offering.
	 * @see Offering#STATUS_PENDING
	 * @see Offering#STATUS_CANCELLED
	 * @see Offering#STATUS_CONFIRMED
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status of the offering
	 * 
	 * @see Offering#STATUS_PENDING
	 * @see Offering#STATUS_CANCELLED
	 * @see Offering#STATUS_CONFIRMED
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the last date on which the offering may be edited. When we reach t=-3
	 *         days before offering start, the offering may no longer be edited
	 *         (since it will have been confirmed or cancelled).
	 */
	public Date getLastEditableDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getStartDate());
		cal.add(Calendar.DATE, -3);
		return cal.getTime();
	}

	// Helper functions for vaadin columns
	public String getTourGuideName() {
		return this.tourGuide.getName();
	}

	/**
	 * @return The human readable LINE username of the tour guide
	 */
	public String getTourGuideLineId() {
		return this.tourGuide.getLineUsername();
	}

	public String getTourName() {
		return this.tour.getTourName();
	}

	/**
	 * @return A nicely formatted string of the start date of the offering
	 */
	public String getStartDateString() {
		return Utils.simpleDateFormat(this.getStartDate());
	}

	@Override
	public String toString() {
		try {
			return String.format("Offering[id=%d, %s, tour=%s]", id, Utils.simpleDateFormat(startDate),
					this.tour.getTourName());
		} catch (NullPointerException e) {
			return "null";
		}
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Offering)
			return other != null && this.getId() != null && ((Offering) other).getId().equals(this.getId());
		return false;
	}

}
