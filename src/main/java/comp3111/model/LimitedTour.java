package comp3111.model;

import java.util.Collection;
import java.util.Date;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;

@Entity
@Inheritance
public class LimitedTour extends Tour {

	//https://stackoverflow.com/questions/6164123/org-hibernate-mappingexception-could-not-determine-type-for-java-util-set
	@ElementCollection(targetClass=Date.class)
	private Collection<Date> datesOffered;

	public Collection<Date> getDatesOffered() {
		return datesOffered;
	}

	public void setDatesOffered(Collection<Date> datesOffered) {
		this.datesOffered = datesOffered;
	}

}
