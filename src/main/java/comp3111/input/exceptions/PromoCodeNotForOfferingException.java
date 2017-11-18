package comp3111.input.exceptions;

/**
 * An exception that is thrown when a customer tries to use a promo code that is not applicable for the
 * offering that he/she is trying to book.
 * 
 * @author kristiansuhartono
 *
 */
public class PromoCodeNotForOfferingException extends Exception {

	public PromoCodeNotForOfferingException() {
		super();
	}

	public PromoCodeNotForOfferingException(String message) {
		super(message);
	}

	public PromoCodeNotForOfferingException(String message, Throwable cause) {
		super(message, cause);
	}

	public PromoCodeNotForOfferingException(Throwable cause) {
		super(cause);
	}

}
