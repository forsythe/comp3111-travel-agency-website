package comp3111.data.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;

/**
 * An abstract class representing either a customer or a tour guide in the
 * database.
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

	public Person() {
	}

	public Person(String name) {

		this.name = name;
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

	@Override
	public String toString() {
		return String.format("Person[id=%d, name='%s']", id, name);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Person)
			return ((Person) other).getId().equals(this.getId());
		return false;
	}

}
