package comp3111.model;

import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;

@Entity
@Inheritance
public class TourGuide extends Person {

	@OneToMany(mappedBy = "tourGuide")
	private Collection<Offering> guidedOfferings;

	public Collection<Offering> getGuidedOfferings() {
		return guidedOfferings;
	}

	public void setGuidedOfferings(Collection<Offering> guidedOfferings) {
		this.guidedOfferings = guidedOfferings;
	}

	// http://www.java2s.com/Tutorial/Java/0355__JPA/OneToManyListCollection.htm
	public void addGuidedOffering(Offering offering) {
		if (!getGuidedOfferings().contains(offering)) {
			getGuidedOfferings().add(offering);
			if (offering.getTourGuide() != null) { // should never be true
				offering.getTourGuide().getGuidedOfferings().remove(offering);
			}
			offering.setTourGuide(this);
		}
	}

	@Override
	public String toString() {
		return String.format("TourGuide[id=%d, name='%s']", getId(), getName());
	}

}
