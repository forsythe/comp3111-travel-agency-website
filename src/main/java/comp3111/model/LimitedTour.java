package comp3111.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;

@Entity
@Inheritance
public class LimitedTour extends Tour {

	// https://stackoverflow.com/questions/6164123/org-hibernate-mappingexception-could-not-determine-type-for-java-util-set
	@ElementCollection(targetClass = Date.class)
	private Collection<Date> datesOffered = new ArrayList<Date>();

	public LimitedTour() {

	}

	public LimitedTour(String tourName, String description, int days, double childDiscount, double toddlerDiscount,
			int weekdayPrice, int weekendPrice, Collection<Date> datesOffered) {
		super(tourName, description, days, childDiscount, toddlerDiscount, weekdayPrice, weekendPrice);
		setDatesOffered(datesOffered);
	}

	public Collection<Date> getDatesOffered() {
		return datesOffered;
	}

	public void setDatesOffered(Collection<Date> datesOffered) {
		this.datesOffered = datesOffered;
	}

}
