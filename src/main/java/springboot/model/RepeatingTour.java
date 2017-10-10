package springboot.model;

import java.util.Collection;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;

@Entity
@Inheritance
public class RepeatingTour extends Tour {

	@ElementCollection(targetClass=String.class)
	private Collection<String> daysOffered;

	public Collection<String> getDaysOffered() {
		return daysOffered;
	}

	public void setDaysOffered(Collection<String> daysOffered) {
		this.daysOffered = daysOffered;
	}

}
