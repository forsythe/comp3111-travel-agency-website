package comp3111.model;

import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Calendar;

@Entity
@Transactional
public class CustomerOffering implements Serializable, Persistable<Long> {

	@Id
	@GeneratedValue
	private long id;

	@ManyToOne(cascade = CascadeType.ALL)
	// @JoinColumn(name = "customerId", referencedColumnName = "id")
	private Customer customer;
	@ManyToOne(cascade = CascadeType.ALL)
	// @JoinColumn(name = "offeringId", referencedColumnName = "id")
	private Offering offering;

	private int numAdults;
	private int numChildren;
	private int numToddlers;
	// private double totalCost;
	private double amountPaid;
	private String specialRequests;
	private String paymentStatus;

	public CustomerOffering() {

	}

	public CustomerOffering(Customer customer, Offering offering, int numAdults, int numChildren, int numToddlers,
			double amountPaid, String specialRequests, String paymentStatus) {
		this.customer = customer;
		this.offering = offering;
		this.numAdults = numAdults;
		this.numChildren = numChildren;
		this.numToddlers = numToddlers;
		this.amountPaid = amountPaid;
		this.specialRequests = specialRequests;
		this.paymentStatus = paymentStatus;
	}

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCustomerName() {
		return customer != null ? customer.getName() : null;
	}
	
	public String getCustomerHkid() {
		return customer != null ? customer.getHkid() : null;
	}
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Long getOfferingId() {
		return getOffering() != null ? getOffering().getId() : null;
	}
	
	public Long getTourId() {
		if(getOffering() != null) {
			if(getOffering().getTour() != null) {
				return getOffering().getTour().getId();
			}
		}
		return null;
	}
	
	public String getTourName() {
		if(getOffering() != null) {
			if(getOffering().getTour() != null) {
				return getOffering().getTour().getTourName();
			}
		}
		return null;
	}
	
	public Offering getOffering() {
		return offering;
	}

	public void setOffering(Offering offering) {
		this.offering = offering;
	}

	public String getPeople() {
		return (this.getNumAdults() + ", " + this.getNumChildren() + ", " + this.getNumToddlers());
	}
	
	public int getNumAdults() {
		return numAdults;
	}

	public void setNumAdults(int numAdults) {
		this.numAdults = numAdults;
	}

	public int getNumChildren() {
		return numChildren;
	}

	public void setNumChildren(int numChildren) {
		this.numChildren = numChildren;
	}

	public int getNumToddlers() {
		return numToddlers;
	}

	public void setNumToddlers(int numToddlers) {
		this.numToddlers = numToddlers;
	}

	public double getTotalCost() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(offering.getStartDate());
		int day = cal.get(Calendar.DAY_OF_WEEK);
		int price;
		if (day >= Calendar.MONDAY && day <= Calendar.FRIDAY) {
			price = this.offering.getTour().getWeekdayPrice();
		} else {
			price = this.offering.getTour().getWeekendPrice();
		}
		return numAdults * price + numChildren * price * this.offering.getTour().getChildDiscount()
				+ numToddlers * price * this.offering.getTour().getToddlerDiscount();
	}

	public double getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(double amountPaid) {
		this.amountPaid = amountPaid;
	}

	public String getSpecialRequests() {
		return specialRequests;
	}

	public void setSpecialRequests(String specialRequests) {
		this.specialRequests = specialRequests;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	@Override
	public String toString() {
		return offering.getStartDate().toString();
	}

	@Override
	/*
	 * This is needed in order to save new CustomerOffering objects with existing
	 * customers and existing offerings hibernate will complain that you're trying
	 * to save "already saved" (detached) customers otherwise
	 * https://stackoverflow.com/questions/16559407/spring-data-jpa-save-new-entity-referencing-existing-one
	 */
	public boolean isNew() {
		return null == getId() && customer.getId() == null && offering.getId() == null;
	}

}
