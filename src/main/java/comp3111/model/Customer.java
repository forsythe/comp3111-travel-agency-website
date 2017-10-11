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
	private String HKID;

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	private Collection<CustomerOffering> customerOffering;

	protected Customer() { // needed to be a bean
	}

	public Customer(String name, String age) {
		setName(name);
		setAge(Integer.parseInt(age));
	}

	public Customer(String name, int age) {
		setName(name);
		setAge(age);
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

	public String getHKID() {
		return HKID;
	}

	public void setHKID(String hKID) {
		HKID = hKID;
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

}
