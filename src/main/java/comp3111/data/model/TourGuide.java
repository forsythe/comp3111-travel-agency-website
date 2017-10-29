package comp3111.data.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;

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