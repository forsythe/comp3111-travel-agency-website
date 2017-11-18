package comp3111.data.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import comp3111.Utils;

/**
 * Represents a promo event associated with an offering. Automatically triggers
 * at {@link PromoEvent#getTriggerDate()}, and will send out a custom message to
 * all known customers.
 * 
 * @author Forsythe
 *
 */
@Entity
public class PromoEvent {

	@Id
	@GeneratedValue
	private Long id;

	private Date triggerDate;
	private String customMessage;
	private String promoCode;

	/**
	 * [0, 1.0]
	 */
	private double discount;

	/**
	 * One this reaches 0, the promo code can no longer be redeemed
	 */
	private int promoCodeUsesLeft;

	/**
	 * For each customer who tries to redeem this promo code, how many people can
	 * they book for at most? (i.e. including adults, children, toddlers)
	 */
	private int maxReservationsPerCustomer;

	/**
	 * Keep track of whether we've announced the custom message yet
	 */
	private boolean isTriggered;

	@OneToOne(fetch = FetchType.EAGER) // one promoevent to 1 offering
	private Offering offering;

	public PromoEvent() {

	}

	/**
	 * Constructs a transient PromoEvent object.
	 * 
	 * @param triggerDate
	 *            The date and time at which the promotional message will be
	 *            broadcasted to all customers
	 * @param customMessage
	 *            A custom message to be sent to everyone
	 * @param promoCode
	 *            A promo code which can be used to make a booking at a discount
	 *            price
	 * @param discount
	 *            A double between [0, 1.0] indicating the discount amount. E.g. 1.0
	 *            for full price (no discount)
	 * @param promoCodeUsesLeft
	 *            How many times this promo code may be used
	 * @param maxReservationsPerCustomer
	 *            The maximum number of spots each customer may reserve when making
	 *            a booking (i.e. {@link Booking#getTotalNumberOfPeople()} can be at
	 *            most this value)
	 * @param offering
	 *            The offering for which this promo code can be used to make
	 *            bookings for
	 */
	public PromoEvent(Date triggerDate, String customMessage, String promoCode, double discount, int promoCodeUsesLeft,
			int maxReservationsPerCustomer, Offering offering) {
		this.triggerDate = triggerDate;
		this.customMessage = customMessage;
		this.promoCode = promoCode;
		this.discount = discount;
		this.promoCodeUsesLeft = promoCodeUsesLeft;
		this.maxReservationsPerCustomer = maxReservationsPerCustomer;
		this.offering = offering;
		isTriggered = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getTriggerDate() {
		return triggerDate;
	}

	public void setTriggerDate(Date triggerDate) {
		this.triggerDate = triggerDate;
	}

	/**
	 * @return A nicely formatted string representing the date at which the
	 *         promotional event will trigger, and the custom message will be sent
	 *         to everyone.
	 */
	public String getTriggerDateString() {
		return Utils.simpleDateFormat(triggerDate);
	}

	public String getCustomMessage() {
		return customMessage;
	}

	public void setCustomMessage(String customMessage) {
		this.customMessage = customMessage;
	}

	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	/**
	 * @return A double between [0, 1.0] indicating the discount multiplier. 1.0
	 *         indicates full price (no discount).
	 */
	public double getDiscount() {
		return discount;
	}

	/**
	 * @param discount
	 *            A double between [0, 1.0] indicating the discount multiplier. 1.0
	 *            indicates full price (no discount).
	 */
	public void setDiscount(double discount) {
		this.discount = discount;
	}

	/**
	 * @return How many more times this promo code may be used. When it reaches 0,
	 *         the promo event is considered to have ended.
	 */
	public int getPromoCodeUsesLeft() {
		return promoCodeUsesLeft;
	}

	/**
	 * @param promoCodeUsesLeft
	 *            How many times this promo code may be used.
	 */
	public void setPromoCodeUsesLeft(int promoCodeUsesLeft) {
		this.promoCodeUsesLeft = promoCodeUsesLeft;
	}

	/**
	 * @return The maximum number of spots each customer may reserve when making a
	 *         booking using this promo code (i.e. {@link Booking#getTotalNumberOfPeople()} can be at most
	 *         this value)
	 */
	public int getMaxReservationsPerCustomer() {
		return maxReservationsPerCustomer;
	}

	/**
	 * @param maxReservationsPerCustomer
	 *            The maximum number of spots each customer may reserve when making
	 *            a booking using this promo code (i.e. {@link Booking#getTotalNumberOfPeople()} can be at
	 *            most this value)
	 */
	public void setMaxReservationsPerCustomer(int maxReservationsPerCustomer) {
		this.maxReservationsPerCustomer = maxReservationsPerCustomer;
	}

	public Offering getOffering() {
		return offering;
	}

	public long getOfferingId() {
		return offering.getId();
	}

	public void setOffering(Offering offering) {
		this.offering = offering;
	}

	/**
	 * @return Whether or not the event has already been triggered, and the custom
	 *         message delivered to everyone.
	 */
	public boolean isTriggered() {
		return isTriggered;
	}
	

	/**
	 * @param hasTriggered
	 *            Whether or not the event has already been triggered, and the
	 *            custom message delivered to everyone.
	 */
	public void setTriggered(boolean hasTriggered) {
		this.isTriggered = hasTriggered;
	}

	@Override
	public String toString() {
		return String.format("PromoEvent[id=%d, offeringId=%d, date=%s]", id, offering.getId(), triggerDate);
	}

}
