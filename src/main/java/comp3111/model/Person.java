package comp3111.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;

@Entity
@Inheritance
public abstract class Person {

	@Id
	@GeneratedValue
	private Long id;

	private String name;
	private String lineId;

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

}
