package comp3111.data.model;

import comp3111.Utils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

/**
 * Represents a Tour in the database. There are two types of tours,
 * {@link #LIMITED_TOUR_TYPE} and {@link #REPEATING_TOUR_TYPE}.
 * {@link #LIMITED_TOUR_TYPE} type tours may only have offerings on
 * {@link #getAllowedDates()}, whereas {@link #REPEATING_TOUR_TYPE} type tours
 * may only have offerings on {@link #getAllowedDaysOfWeek()}.
 * 
 * @author Forsythe
 *
 */
@Entity
public class Tour {

	@Id
	@GeneratedValue
	private Long id;

	private String tourName;
	private String description;
	private int days;

	@ElementCollection(targetClass = Integer.class)
	@LazyCollection(LazyCollectionOption.FALSE)
	private Collection<Integer> allowedDaysOfWeek = new HashSet<Integer>();
	// for an offering of this tour, is it allowed on MONDAY, TUESDAY, etc

	@ElementCollection(targetClass = Date.class)
	@LazyCollection(LazyCollectionOption.FALSE)
	private Collection<Date> allowedDates = new HashSet<Date>();
	// for an offering of this tour, is it only allowed on specific dates
	// null for ANY date

	private double childDiscount;
	private double toddlerDiscount;
	private int weekdayPrice;
	private int weekendPrice;

	private boolean isChildFriendly;

	public static final String LIMITED_TOUR_TYPE = "Limited";

	public static final String REPEATING_TOUR_TYPE = "Repeating";

	public Tour() {
	}

	/**
	 * Constructs a transient Tour object, which is not child-friendly.
	 * 
	 * @param tourName
	 *            The name of the tour
	 * @param description
	 *            The user-facing description of the tour, which can at most be 255
	 *            characters long
	 * @param days
	 *            The duration of every offering associated with this tour.
	 * @param childDiscount
	 *            A double between [0, 1.0] indicating the discount for children.
	 *            1.0 for no discount.
	 * @param toddlerDiscount
	 *            A double between [0, 1.0] indicating the discount for toddlers.
	 *            1.0 for no discount.
	 * @param weekdayPrice
	 *            The dollar weekday price. Offerings for this tour created on
	 *            weekdays will use this price.
	 * @param weekendPrice
	 *            The dollar weekend price. Offerings for this tour created on
	 *            weekends will use this price.
	 */
	public Tour(String tourName, String description, int days, double childDiscount, double toddlerDiscount,
			int weekdayPrice, int weekendPrice) {
		super();
		this.tourName = tourName;
		this.description = description;
		this.days = days;
		this.childDiscount = childDiscount;
		this.toddlerDiscount = toddlerDiscount;
		this.weekdayPrice = weekdayPrice;
		this.weekendPrice = weekendPrice;
		this.isChildFriendly = false;
	}

	/**
	 * Constructs a transient Tour object.
	 * 
	 * @param tourName
	 *            The name of the tour
	 * @param description
	 *            The user-facing description of the tour, which can at most be 255
	 *            characters long
	 * @param days
	 *            The duration of every offering associated with this tour.
	 * @param childDiscount
	 *            A double between [0, 1.0] indicating the discount for children.
	 *            1.0 for no discount.
	 * @param toddlerDiscount
	 *            A double between [0, 1.0] indicating the discount for toddlers.
	 *            1.0 for no discount.
	 * @param weekdayPrice
	 *            The dollar weekday price. Offerings for this tour created on
	 *            weekdays will use this price.
	 * @param weekendPrice
	 *            The dollar weekend price. Offeirngs for this tour created on
	 *            weekends will use this price.
	 * @param isChildFriendly
	 *            Whether or not this Tour is child friendly. Not used for any
	 *            constraints checking. Only used for making recommendations to
	 *            customers.
	 */
	public Tour(String tourName, String description, int days, double childDiscount, double toddlerDiscount,
			int weekdayPrice, int weekendPrice, boolean isChildFriendly) {
		super();
		this.tourName = tourName;
		this.description = description;
		this.days = days;
		this.childDiscount = childDiscount;
		this.toddlerDiscount = toddlerDiscount;
		this.weekdayPrice = weekdayPrice;
		this.weekendPrice = weekendPrice;
		this.isChildFriendly = isChildFriendly;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTourName() {
		return tourName;
	}

	public void setTourName(String tourName) {
		this.tourName = tourName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return How many days an offering of this tour will last
	 */
	public int getDays() {
		return this.days;
	}

	/**
	 * @param days
	 *            How many days an offering of this tour will last
	 */
	public void setDays(int days) {
		this.days = days;
	}

	/**
	 * @return A collection of integers representing on which days of the week
	 *         offerings may be created for this tour. Follows the convention of
	 *         java.util.Calendar.
	 * @see Utils#dayToString(int)
	 * @see Utils#integerSetToStringDayNameSet(Collection)
	 */
	public Collection<Integer> getAllowedDaysOfWeek() {
		return this.allowedDaysOfWeek;
	}

	/**
	 * Sets the days of week on which this tour may have offerings. If this value is
	 * set (with a non-empty collection), then the tour type is assumed to be
	 * {@link #REPEATING_TOUR_TYPE}. Therefore, do not call
	 * {@link #setAllowedDates(Collection)}, which is for {@link #LIMITED_TOUR_TYPE}
	 * type tours.
	 * 
	 * @param allowedDaysOfWeek
	 *            A collection of integers representing on which days of the week
	 *            offerings may be created for this tour. Follows the convention of
	 *            java.util.Calendar.
	 */
	public void setAllowedDaysOfWeek(Collection<Integer> allowedDaysOfWeek) {
		if (allowedDaysOfWeek != null)
			this.allowedDaysOfWeek = allowedDaysOfWeek;
	}

	/**
	 * @return A collection of dates representing on which actual dates offerings
	 *         may be created for this tour.
	 */
	public Collection<Date> getAllowedDates() {
		return allowedDates;
	}

	/**
	 * Sets the actual dates on which this tour may have offerings. If this value is
	 * set (with a non-empty collection), then the tour type is assumed to be
	 * {@link #LIMITED_TOUR_TYPE}. Therefore, do not call
	 * {@link #setAllowedDaysOfWeek(Collection)} which is for
	 * {@link #REPEATING_TOUR_TYPE} type tours.
	 * 
	 * @param allowedDates
	 *            A collection of integers representing on which days of the week
	 *            offerings may be created for this tour. Follows the convention of
	 *            java.util.Calendar.
	 */
	public void setAllowedDates(Collection<Date> allowedDates) {
		if (allowedDates == null)
			this.allowedDates = new HashSet<Date>();
		else
			this.allowedDates = allowedDates;
	}

	/**
	 * @return A double between [0, 1.0]. 1.0 indicates full price (no discount).
	 */
	public double getChildDiscount() {
		return childDiscount;
	}

	/**
	 * @param childDiscount
	 *            A double between [0, 1.0]. 1.0 indicates full price (no discount).
	 */
	public void setChildDiscount(double childDiscount) {
		this.childDiscount = childDiscount;
	}

	/**
	 * @return A double between [0, 1.0]. 1.0 indicates full price (no discount).
	 */
	public double getToddlerDiscount() {
		return toddlerDiscount;
	}

	/**
	 * @param toddlerDiscount
	 *            A double between [0, 1.0]. 1.0 indicates full price (no discount).
	 */
	public void setToddlerDiscount(double toddlerDiscount) {
		this.toddlerDiscount = toddlerDiscount;
	}

	public int getWeekdayPrice() {
		return weekdayPrice;
	}

	public void setWeekdayPrice(int weekdayPrice) {
		this.weekdayPrice = weekdayPrice;
	}

	public int getWeekendPrice() {
		return weekendPrice;
	}

	public void setWeekendPrice(int weekendPrice) {
		this.weekendPrice = weekendPrice;
	}

	/**
	 * @return Whether or not the tour is child friendly. Is not used for any strict
	 *         enforcing, but only as suggestions to customers when they ask for
	 *         recommendations.
	 */
	public boolean isChildFriendly() {
		return isChildFriendly;
	}

	/**
	 * @param isChildFriendly
	 *            Whether or not the tour is child friendly. Is not used for any
	 *            strict enforcing, but only as suggestions to customers when they
	 *            ask for recommendations.
	 */
	public void setChildFriendly(boolean isChildFriendly) {
		this.isChildFriendly = isChildFriendly;
	}

	/**
	 * @return A nicely formatted string representing the dates or days of the week
	 *         on which offerings may be created for this tour. If this is a
	 *         {@link #LIMITED_TOUR_TYPE} tour, then the function returns a
	 *         collection of dates. Otherwise if it is a
	 *         {@link #REPEATING_TOUR_TYPE}, it returns a collection of integers
	 *         representing the days of the week.
	 */
	public String getOfferingAvailability() {
		if (!allowedDates.isEmpty())
			return this.getFormattedAllowedDates();
		else
			return this.getFormattedAllowedDaysOfWeek();
	}

	private String getFormattedAllowedDates() {
		return Utils.dateCollectionToString(allowedDates);
	}

	private String getFormattedAllowedDaysOfWeek() {
		return Utils.integerCollectionToString(allowedDaysOfWeek);
	}

	@Override
	public String toString() {
		return String.format("Tour[id=%d, tourName=%s]", id, tourName);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Tour)
			return other != null && this.getId() != null && ((Tour) other).getId().equals(this.getId());
		return false;
	}

}
