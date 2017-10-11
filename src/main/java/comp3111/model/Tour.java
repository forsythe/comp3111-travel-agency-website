package comp3111.model;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;

@Entity
@Inheritance
public abstract class Tour {

	@Id
	@GeneratedValue
	private Long id;

	private String tourName;
	private String description;
	private int days;

	@OneToMany(mappedBy = "tour")
	private Collection<Offering> offerings;

	private double childDiscount;
	private double toddlerDiscount;
	private int weekdayPrice;
	private int weekendPrice;

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

	public void setOfferings(Collection<Offering> offerings) {
		this.offerings = offerings;
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
		if (!getOfferings().contains(offering)) {
			getOfferings().add(offering);
			if (offering.getTour() != null) { // should never be true
				offering.getTour().getOfferings().remove(offering);
			}
			offering.setTour(this);
		}
	}

	@Override
	public String toString() {
		return String.format("Tour[id=%d, tourName='%s']", id, tourName);
	}

}
