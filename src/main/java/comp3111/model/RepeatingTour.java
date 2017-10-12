package comp3111.model;

import java.time.DayOfWeek;
import java.util.Collection;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;

@Entity
@Inheritance
public class RepeatingTour extends Tour {

	@ElementCollection(targetClass = DayOfWeek.class)
	private Collection<DayOfWeek> daysOffered;

	public RepeatingTour() {
	}

	public RepeatingTour(String tourName, String description, int days, Collection<Offering> offerings,
			double childDiscount, double toddlerDiscount, int weekdayPrice, int weekendPrice,
			Collection<DayOfWeek> daysOffered) {
		super(tourName, description, days, offerings, childDiscount, toddlerDiscount, weekdayPrice, weekendPrice);
		setDaysOffered(daysOffered);
	}

	public Collection<DayOfWeek> getDaysOffered() {
		return daysOffered;
	}

	public void setDaysOffered(Collection<DayOfWeek> daysOffered) {
		this.daysOffered = daysOffered;
	}

}
