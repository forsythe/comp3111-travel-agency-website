package comp3111.model;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.transaction.Transactional;

@Entity
@Transactional
public class CustomerOffering implements Serializable {

	@Id
	@GeneratedValue
	private long id;

	@ManyToOne(cascade = CascadeType.PERSIST)
	// @JoinColumn(name = "customerId", referencedColumnName = "id")
	private Customer customer;
	@ManyToOne(cascade = CascadeType.MERGE)
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

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Offering getOffering() {
		return offering;
	}

	public void setOffering(Offering offering) {
		this.offering = offering;
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
}
