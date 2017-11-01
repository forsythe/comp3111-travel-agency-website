package comp3111.data.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;

/**
 * Represents a customer entity in the databse
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

	public Customer() { // needed to be a bean
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

	@Override
	public String toString() {
		return String.format("Customer[%s, id=%d]", getName(), getId());
	}

	/**
	 * @param status
	 *            the status of the tour (e.g. confirmed, cancelled, etc.) Will be a
	 *            string which is sent directly to the customer via their line id to
	 *            their phone
	 * @see #getLineId()
	 */
	public void updateAboutOfferingStatus(String status) {
		// do something with status
		// e.g. get the line ID and send out the status
		// TODO
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}
}
