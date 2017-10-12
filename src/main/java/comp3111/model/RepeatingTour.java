package comp3111.model;

import java.util.Collection;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.transaction.Transactional;

@Entity
@Inheritance
@Transactional
public class RepeatingTour extends Tour {

	@ElementCollection(targetClass = Integer.class)
	private Collection<Integer> daysOffered;

	public RepeatingTour() {
	}

	public RepeatingTour(String tourName, String description, int days, double childDiscount, double toddlerDiscount,
			int weekdayPrice, int weekendPrice, Collection<Integer> daysOffered) {
		super(tourName, description, days, childDiscount, toddlerDiscount, weekdayPrice, weekendPrice);
		setDaysOffered(daysOffered);
	}

	public Collection<Integer> getDaysOffered() {
		return daysOffered;
	}

	public void setDaysOffered(Collection<Integer> daysOffered) {
		this.daysOffered = daysOffered;
	}

}
