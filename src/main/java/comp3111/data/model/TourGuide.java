package comp3111.data.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.domain.Persistable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.HashSet;

@Entity
@Inheritance
public class TourGuide extends Person {

	public TourGuide() {
	}

	public TourGuide(String name, String lineId) {
		super(name, lineId);
	}

	@Override
	public String toString() {
		return String.format("TourGuide[id=%d, name='%s']", getId(), getName());
	}

}