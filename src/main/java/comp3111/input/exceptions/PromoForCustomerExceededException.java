package comp3111.input.exceptions;

/**
 * An exception that is thrown when a customer is trying to book an exception using a promo code but
 * the number of the customer is trying to book with is more than the amount the promo code allows per
 * customer
 * @author kristiansuhartono
 *
 */
public class PromoForCustomerExceededException extends Exception {

	public PromoForCustomerExceededException() {
		super();
	}

	public PromoForCustomerExceededException(String message) {
		super(message);
	}

	public PromoForCustomerExceededException(String message, Throwable cause) {
		super(message, cause);
	}

	public PromoForCustomerExceededException(Throwable cause) {
		super(cause);
	}

}
