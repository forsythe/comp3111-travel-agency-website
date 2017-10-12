package comp3111.model;

import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;

@Entity
@Inheritance
public class Customer extends Person {

	private String phone;
	private int age;
	private String hkid;

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	private Collection<CustomerOffering> customerOffering;

	protected Customer() { // needed to be a bean
	}

	public Customer(String name, int age) {
		super(name, null);
		this.age = age;
	}

	public Customer(String name, String lineId, String phone, int age, String hkid,
			Collection<CustomerOffering> customerOffering) {
		super(name, lineId);
		this.phone = phone;
		this.age = age;
		this.hkid = hkid;
		this.customerOffering = customerOffering;
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
		return String.format("Customer[id=%d, name='%s', age='%s']", getId(), getName(), age);
	}

	public Collection<CustomerOffering> getCustomerOffering() {
		return customerOffering;
	}

	public void setCustomerOffering(Collection<CustomerOffering> customerOffering) {
		this.customerOffering = customerOffering;
	}

	public void updateAboutOfferingStatus(String status) {
		// do something with status
		// e.g. get the line ID and send out the status
		// TODO
	}

}
