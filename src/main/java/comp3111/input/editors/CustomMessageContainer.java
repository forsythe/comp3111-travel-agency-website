/**
 * 
 */
package comp3111.input.editors;

import com.vaadin.ui.TextArea;

import comp3111.data.model.Offering;

/**
 * Used to store the custom message when creating a promotional event. When an
 * object of this instance is updated (via setters), the corresponding
 * TextArea's value will also be updated
 * 
 * @author Forsythe
 *
 */
public class CustomMessageContainer {

	private Offering offering;
	private String discountMultiplier;
	private String maxReservationsPerCustomer;
	private String promoCode;
	private String promoCodeUses;

	private TextArea textArea;

	/**
	 * Construct a new CustomMessageContainer which is bound to a vaadin text area.
	 * 
	 * @param tA The TextArea which gets updated when we update
	 */
	public CustomMessageContainer(TextArea tA) {
		this.textArea = tA;
	}

	/**
	 * @param offering
	 *            the offering to set
	 */
	public void setOffering(Offering offering) {
		this.offering = offering;
		onChange();
	}

	/**
	 * @param discountMultiplier
	 *            the discountMultiplier to set
	 */
	public void setDiscountMultiplier(String discountMultiplier) {
		this.discountMultiplier = discountMultiplier;
		onChange();
	}

	/**
	 * @param maxReservationsPerCustomer
	 *            the maxReservationsPerCustomer to set
	 */
	public void setMaxReservationsPerCustomer(String maxReservationsPerCustomer) {
		this.maxReservationsPerCustomer = maxReservationsPerCustomer;
		onChange();
	}

	/**
	 * @param promoCode
	 *            the promoCode to set
	 */
	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
		onChange();
	}

	/**
	 * @param promoCodeUses
	 *            the promoCodeUses to set
	 */
	public void setPromoCodeUses(String promoCodeUses) {
		this.promoCodeUses = promoCodeUses;
		onChange();
	}

	private String getCustomMessage() {

		try {
			if (Double.parseDouble(discountMultiplier) < 0 || Double.parseDouble(discountMultiplier) > 1)
				return "";
			//catching parse exceptions
			Integer.parseInt(promoCodeUses);
			Integer.parseInt(maxReservationsPerCustomer);

			StringBuilder sb = new StringBuilder();
			sb.append(String.format("New promo for %s!", this.offering));
			sb.append(String.format(" Book with promo code [%s] for %.0f%% off, while offers last.", this.promoCode,
					(1 - Double.parseDouble(discountMultiplier)) * 100));
			sb.append(
					String.format(" Max %d reservations per customer.", Integer.parseInt(maxReservationsPerCustomer)));
			return sb.toString();
		} catch (Exception e) {
			return ""; // parse error
		}
	}

	private void onChange() {
		this.textArea.setValue(getCustomMessage());
	}

}
