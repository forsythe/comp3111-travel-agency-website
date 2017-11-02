package comp3111.data.model;

import comp3111.Utils;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Represents an offering entity in the database. One tour may have multiple
 * offerings. Offerings can be limited or repeating, depending on their
 * constraints {@link Tour#getAllowedDaysOfWeek()} and
 * {@link Tour#getAllowedDates()})
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
	}

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
	 * @return the status of the offering, i.e. {@link Offering#STATUS_CANCELLED},
	 *         {@link Offering#STATUS_PENDING}, or {@link Offering#STATUS_CONFIRMED}
	 */
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the last date on which the offering may be edited. When the offering
	 *         is about to happen within 3 days, the offering is no longer editable.
	 */
	public Date getLastEditableDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getStartDate());
		cal.add(Calendar.DATE, -3);
		return cal.getTime();
	}

	@Override
	public String toString() {
		return String.format("Offering[id=%d, %s, %s]", id, Utils.simpleDateFormat(startDate), this.tour.getTourName());
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Offering)
			return other != null && this.getId() != null && ((Offering) other).getId().equals(this.getId());
		return false;
	}

	// Helper functions for vaadin columns
	public String getTourGuideName() {
		return this.tourGuide.getName();
	}

	public String getTourGuideLineId() {
		return this.tourGuide.getLineUsername();
	}

	public String getTourName() {
		return this.tour.getTourName();
	}

	public String getStartDateString() {
		return Utils.simpleDateFormat(this.getStartDate());
	}

}
