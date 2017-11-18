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

// --Commented out by Inspection START (18/11/17 14:45):
//	public PromoForCustomerExceededException(String message) {
//		super(message);
//	}
// --Commented out by Inspection STOP (18/11/17 14:45)

// --Commented out by Inspection START (18/11/17 14:45):
//	public PromoForCustomerExceededException(String message, Throwable cause) {
//		super(message, cause);
//	}
// --Commented out by Inspection STOP (18/11/17 14:45)

// --Commented out by Inspection START (18/11/17 14:45):
//	public PromoForCustomerExceededException(Throwable cause) {
//		super(cause);
//	}
// --Commented out by Inspection STOP (18/11/17 14:45)

}
