package comp3111.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.transaction.Transactional;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.domain.Persistable;

import comp3111.validators.Utils;

@Entity
@Transactional
public class Tour implements Persistable<Long> {

	@Id
	@GeneratedValue
	private Long id;

	private String tourName;
	private String description;
	private int days;

	@OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	private Collection<Offering> offerings = new HashSet<Offering>();

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
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public Collection<Offering> getOfferings() {
		return offerings;
	}

	public String getOfferingsString() {
		String ans = "[";

		for (Offering x : offerings) {
			ans += x.toString() + ", ";
		}
		return ans.substring(0, ans.length() - 2) + "]";
	}

	public void setOfferings(Collection<Offering> offerings) {
		this.offerings = offerings;
	}

	public Collection<Integer> getAllowedDaysOfWeek() {
		return allowedDaysOfWeek;
	}

	private ArrayList<String> getFormattedAllowedDaysOfWeek() {
		ArrayList<String> ans = new ArrayList<>();
		for (int day : allowedDaysOfWeek) {
			ans.add(Utils.dayToString(day));
		}
		return ans;
	}

	public ArrayList<String> getOfferingAvailability() {
		if (!allowedDates.isEmpty())
			return this.getFormattedAllowedDates();
		else
			return this.getFormattedAllowedDaysOfWeek();
	}

	public String getOfferingAvailabilityString() {
		if (!allowedDates.isEmpty())
			return this.getFormattedAllowedDates().toString();
		else
			return this.getFormattedAllowedDaysOfWeek().toString();
	}

	public void setAllowedDaysOfWeek(Collection<Integer> allowedDaysOfWeek) {
		this.allowedDaysOfWeek = allowedDaysOfWeek;
	}

	public Collection<Date> getAllowedDates() {
		return allowedDates;
	}

	private ArrayList<String> getFormattedAllowedDates() {
		ArrayList<String> ans = new ArrayList<>();
		for (Date day : allowedDates) {
			ans.add(Utils.simpleDateFormat(day));
		}
		return ans;
	}

	public void setAllowedDates(Collection<Date> allowedDates) {
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

	// http://www.java2s.com/Tutorial/Java/0355__JPA/OneToManyListCollection.htm
	public void addOffering(Offering offering) {
		// if (!getOfferings().contains(offering)) {
		// getOfferings().add(offering);
		// if (offering.getTour() != null) { // should never be true
		// offering.getTour().getOfferings().remove(offering);
		// }
		// offering.setTour(this);
		// }
		offerings.add(offering);
	}

	@Override
	public String toString() {
		return String.format("Tour[id=%d, tourName='%s']", id, tourName);
	}

	@Override
	public boolean isNew() {
		if (null != getId())
			return false;
		for (Offering o : offerings) {
			if (o.getId() != null)
				return false;
		}
		return true;
	}

}
