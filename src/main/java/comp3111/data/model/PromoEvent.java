package comp3111.data.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import comp3111.Utils;

/**
 * represents a promo event associated with an offering
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

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public int getPromoCodeUsesLeft() {
		return promoCodeUsesLeft;
	}

	public void setPromoCodeUsesLeft(int promoCodeUsesLeft) {
		this.promoCodeUsesLeft = promoCodeUsesLeft;
	}

	public int getMaxReservationsPerCustomer() {
		return maxReservationsPerCustomer;
	}

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

	public boolean isTriggered() {
		return isTriggered;
	}
	
	public boolean getTriggered() {
		return isTriggered;
	}

	public void setTriggered(boolean hasTriggered) {
		this.isTriggered = hasTriggered;
	}

	@Override
	public String toString() {
		return String.format("PromoEvent[id=%d, offeringId=%d, date=%s]", id, offering.getId(), triggerDate);
	}

}
