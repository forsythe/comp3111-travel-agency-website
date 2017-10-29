package comp3111.data.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.domain.Persistable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.HashSet;

@Entity
@Inheritance
public class Customer extends Person {

	private String phone;
	private int age;
	private String hkid;

	public Customer() { // needed to be a bean
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

	public void updateAboutOfferingStatus(String status) {
		// do something with status
		// e.g. get the line ID and send out the status
		// TODO
	}

}
