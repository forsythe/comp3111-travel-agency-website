package comp3111.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class CustomerOffering implements Serializable {

	@Id
	@GeneratedValue
	private long id;

	@ManyToOne
	// @JoinColumn(name = "customerId", referencedColumnName = "id")
	private Customer customer;
	@ManyToOne
	// @JoinColumn(name = "offeringId", referencedColumnName = "id")
	private Offering offering;

	private int numAdults;
	private int numChildren;
	private int numToddlers;
	private double totalCost;
	private double amountPaid;
	private String specialRequests;
	private String paymentStatus;

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
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
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
