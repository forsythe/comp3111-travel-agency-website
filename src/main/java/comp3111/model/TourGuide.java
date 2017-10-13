package comp3111.model;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.domain.Persistable;

@Entity
@Inheritance
public class TourGuide extends Person implements Persistable<Long> {

	public TourGuide() {
	}

	public TourGuide(String name, String lineId) {
		super(name, lineId);
	}

	@OneToMany(mappedBy = "tourGuide", cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	private Collection<Offering> guidedOfferings = new HashSet<Offering>();

	public Collection<Offering> getGuidedOfferings() {
		return guidedOfferings;
	}

	public void setGuidedOfferings(Collection<Offering> guidedOfferings) {
		this.guidedOfferings = guidedOfferings;
	}

	// http://www.java2s.com/Tutorial/Java/0355__JPA/OneToManyListCollection.htm
	// public void addGuidedOffering(Offering offering) {
	// // if (!getGuidedOfferings().contains(offering)) {
	// // getGuidedOfferings().add(offering);
	// // if (offering.getTourGuide() != null) { // should never be true
	// // offering.getTourGuide().getGuidedOfferings().remove(offering);
	// // }
	// // offering.setTourGuide(this);
	// // }
	// guidedOfferings.add(offering);
	// }

	@Override
	public String toString() {
		return String.format("TourGuide[id=%d, name='%s']", getId(), getName());
	}

	@Override
	public boolean isNew() {
		if (getId() != null) {
			return false;
		}
		for (Offering o : guidedOfferings) {
			if (o.getId() != null)
				return false;
		}
		return true;
	}

}