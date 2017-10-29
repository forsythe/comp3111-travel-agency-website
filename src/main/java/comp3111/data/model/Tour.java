package comp3111.data.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.domain.Persistable;

import comp3111.Utils;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

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

	public static final String LIMITED_TOUR_TYPE = "Limited";

	public static final String REPEATING_TOUR_TYPE = "Repeating";

	public Tour() {
	}

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

	public int getDays() {
		return this.days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public Collection<Integer> getAllowedDaysOfWeek() {
		return this.allowedDaysOfWeek;
	}

	public void setAllowedDaysOfWeek(Collection<Integer> allowedDaysOfWeek) {
		if (allowedDaysOfWeek == null)
			allowedDaysOfWeek = new HashSet<Integer>();
		else
			this.allowedDaysOfWeek = allowedDaysOfWeek;
	}

	public Collection<Date> getAllowedDates() {
		return allowedDates;
	}

	public void setAllowedDates(Collection<Date> allowedDates) {
		if (allowedDates == null)
			allowedDates = new HashSet<Date>();
		else
			this.allowedDates = allowedDates;
	}

	public double getChildDiscount() {
		return childDiscount;
	}

	public void setChildDiscount(double childDiscount) {
		this.childDiscount = childDiscount;
	}

	public double getToddlerDiscount() {
		return toddlerDiscount;
	}

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

	/*
	 * not an attribute, but helper function for vaadin column
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
