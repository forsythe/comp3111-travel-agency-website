package comp3111.data.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;

/**
 * Represents a customer entity in the database.
 * 
 * @author Forsythe
 *
 */
@Entity
@Inheritance
public class Customer extends Person {

	private String phone;
	private int age;
	private String hkid;
	private String lineId;

	public Customer() {
	}

	public Customer(String name, int age) {
		super(name);
		this.age = age;
	}

	public Customer(String name, String lineId, String phone, int age, String hkid) {
		super(name);
		this.phone = phone;
		this.age = age;
		this.hkid = hkid;
		this.lineId = lineId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getHkid() {
		return hkid;
	}

	public void setHkid(String hkid) {
		this.hkid = hkid;
	}

	/**
	 * @return The internal LINE ID of a user (not the human-readable username, but a long
	 *         string of digits) which can be used by {@link comp3111.LineMessenger}
	 *         to push messages.
	 */
	public String getLineId() {
		return lineId;
	}

	/**
	 * @param lineId
	 *            The internal LINE ID of a user (not the human-readable username,
	 *            but a long string of digits) which can be used by
	 *            {@link comp3111.LineMessenger} to push messages.
	 */
	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	@Override
	public String toString() {
		return String.format("Customer[%s, id=%d]", getName(), getId());
	}

}
