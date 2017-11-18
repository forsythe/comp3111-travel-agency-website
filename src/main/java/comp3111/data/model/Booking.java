package comp3111.data.model;

import javax.persistence.*;
import java.util.Calendar;

/**
 * 
 * This class represents a single booking record between a customer and an offering in the database.
 */
/**
 * @author Forsythe
 *
 */
@Entity
public class Booking {

	public static final String PAYMENT_PENDING = "Pending";
	public static final String PAYMENT_CONFIRMED = "Confirmed";
	public static final String PAYMENT_CANCELLED_BECAUSE_OFFERING_CANCELLED = "Offering cancelled";
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	private Customer customer;
	@ManyToOne(fetch = FetchType.EAGER)
	private Offering offering;

	private int numAdults;
	private int numChildren;
	private int numToddlers;
	private double amountPaid;
	private String specialRequests;
	private String paymentStatus;

	private double promoDiscountMultiplier;

	private String promoCodeUsed;

	public Booking() {
		promoDiscountMultiplier = 1;
	}

	/**
	 * Creates a new transient (not yet saved in DB) booking object without a promo
	 * code.
	 * 
	 * @param customer
	 *            The customer making the booking
	 * @param offering
	 *            The offering to book
	 * @param numAdults
	 *            How many adults in the booking
	 * @param numChildren
	 *            How many children in the booking
	 * @param numToddlers
	 *            How many toddlers in the booking
	 * @param amountPaid
	 *            How much the customer has already paid
	 * @param specialRequests
	 *            Special requests
	 * @param paymentStatus
	 *            The payment status. Can be {@link Booking#PAYMENT_PENDING},
	 *            {@link Booking#PAYMENT_CONFIRMED}, or
	 *            {@link Booking#PAYMENT_CANCELLED_BECAUSE_OFFERING_CANCELLED}
	 */
	public Booking(Customer customer, Offering offering, int numAdults, int numChildren, int numToddlers,
			double amountPaid, String specialRequests, String paymentStatus) {
		this.customer = customer;
		this.offering = offering;
		this.numAdults = numAdults;
		this.numChildren = numChildren;
		this.numToddlers = numToddlers;
		this.amountPaid = amountPaid;
		this.specialRequests = specialRequests;
		this.paymentStatus = paymentStatus;
		this.promoDiscountMultiplier = 1; // no promo discount
		this.promoCodeUsed = null;
	}

	/**
	 * Creates a new transient (not yet saved in DB) booking object, but with a
	 * promo code code.
	 * 
	 * @param customer
	 *            The customer making the booking
	 * @param offering
	 *            The offering to book
	 * @param numAdults
	 *            How many adults in the booking
	 * @param numChildren
	 *            How many children in the booking
	 * @param numToddlers
	 *            How many toddlers in the booking
	 * @param amountPaid
	 *            How much the customer has already paid
	 * @param specialRequests
	 *            Special requests
	 * @param paymentStatus
	 *            The payment status. Can be {@link Booking#PAYMENT_PENDING},
	 *            {@link Booking#PAYMENT_CONFIRMED}, or
	 *            {@link Booking#PAYMENT_CANCELLED_BECAUSE_OFFERING_CANCELLED}
	 * @param promoDiscountMultiplier
	 *            The discount provided by the promotion; a double between [0, 1].
	 *            E.g. 0.7 indicates 30% off the final price.
	 * @param promoCodeUsed
	 *            The actual promo code.
	 */
	public Booking(Customer customer, Offering offering, int numAdults, int numChildren, int numToddlers,
			double amountPaid, String specialRequests, String paymentStatus, double promoDiscountMultiplier,
			String promoCodeUsed) {
		this.customer = customer;
		this.offering = offering;
		this.numAdults = numAdults;
		this.numChildren = numChildren;
		this.numToddlers = numToddlers;
		this.amountPaid = amountPaid;
		this.specialRequests = specialRequests;
		this.paymentStatus = paymentStatus;
		this.promoDiscountMultiplier = promoDiscountMultiplier;
		this.promoCodeUsed = promoCodeUsed;
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

	/**
	 * @return Gets the Id of the tour that this offering belongs to.
	 */
	public Long getTourId() {
		return getOffering().getTour().getId();
	}

	/**
	 * @return The name of the tour that this offering belongs to.
	 */
	public String getTourName() {
		return getOffering().getTour().getTourName();
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

	public int getTotalNumberOfPeople() {
		return numAdults + numChildren + numToddlers;
	}

	public String getPromoCodeUsed() {
		return promoCodeUsed;
	}

	public void setPromoCodeUsed(String promoCodeUsed) {
		this.promoCodeUsed = promoCodeUsed;
	}

	/**
	 * @return The dollar cost of this booking record. Depends on if it's
	 *         weekday/weekend, the number of adults, children, toddlers, child
	 *         discount, and toddler discount. Will also apply the promotional
	 *         discount, if applicable.
	 */
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
		double rawPrice = numAdults * price + numChildren * price * this.offering.getTour().getChildDiscount()
				+ numToddlers * price * this.offering.getTour().getToddlerDiscount();
		return rawPrice * promoDiscountMultiplier;
	}

	/**
	 * @return The dollar amount that's already been paid by the customer
	 */
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

	/**
	 * @return Returns a double [0, 1.0] indicating the discount multiplier. E.g.
	 *         0.7 means a 30% discount.
	 */
	public double getPromoDiscountMultiplier() {
		return promoDiscountMultiplier;
	}

	/**
	 * @param promoDiscountMultiplier
	 *            A double between [0, 1.0] indicating the discount multiplier. 1.0
	 *            for full price (no discount).
	 */
	public void setPromoDiscountMultiplier(double promoDiscountMultiplier) {
		this.promoDiscountMultiplier = promoDiscountMultiplier;
	}

	@Override
	public String toString() {
		return "Booking[" + customer.getName() + ", " + offering + "]";
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Booking)
			return ((Booking) other).getId().equals(this.getId());
		return false;
	}

}
