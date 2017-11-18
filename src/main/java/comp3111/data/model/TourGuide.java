package comp3111.data.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;

/**
 * Represents a tour guide in the database.
 * @author Forsythe
 *
 */
@Entity
@Inheritance
public class TourGuide extends Person {

	private String lineUsername;

	public TourGuide() {
	}

	public TourGuide(String name, String lineUsername) {
		super(name);
		this.lineUsername = lineUsername;
	}

	@Override
	public String toString() {
		return String.format("TourGuide[id=%d, name='%s']", getId(), getName());
	}

	public String getLineUsername() {
		return lineUsername;
	}

	public void setLineUsername(String lineUsername) {
		this.lineUsername = lineUsername;
	}

}