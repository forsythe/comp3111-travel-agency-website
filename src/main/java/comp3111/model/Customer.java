package comp3111.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;
import javax.transaction.Transactional;

import org.springframework.data.domain.Persistable;

@Entity
@Inheritance
@Transactional
public class Customer extends Person implements Persistable<Long> {

	private String phone;
	private int age;
	private String hkid;

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Collection<CustomerOffering> customerOffering = new ArrayList<CustomerOffering>();

	protected Customer() { // needed to be a bean
	}

	public Customer(String name, int age) {
		super(name, null);
		this.age = age;
	}

	public Customer(String name, String lineId, String phone, int age, String hkid) {
		super(name, lineId);
		this.phone = phone;
		this.age = age;
		this.hkid = hkid;
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

	@Override
	public boolean isNew() {
		if (getId() != null) {
			return false;
		}
		for (CustomerOffering co : customerOffering) {
			if (co.getId() != null)
				return false;
		}
		return true;
	}

}
