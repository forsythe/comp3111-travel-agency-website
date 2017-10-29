package comp3111.data.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;

/**
 * An abstract entity either representing a customer or a tour guide in the
 * database
 * 
 * @author Forsythe
 *
 */
@Entity
@Inheritance
public abstract class Person {

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	/**
	 * Refers to the line ID used for programmatically sending out messages to the
	 * user's phone
	 */
	private String lineId;
	
	
	/**
	 * Refers to the human readable line username.
	 */
	private String lineUsername;

	public Person() {
	}

	public Person(String name, String lineId) {
		this.name = name;
		this.lineId = lineId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	@Override
	public String toString() {
		return String.format("Person[id=%d, name='%s']", id, name);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Person)
			return other != null && this.getId() != null && ((Person) other).getId().equals(this.getId());
		return false;
	}

}
