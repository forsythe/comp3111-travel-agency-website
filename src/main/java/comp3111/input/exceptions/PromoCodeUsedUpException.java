package comp3111.input.exceptions;

/**
 * An exception that is thrown when a customer tries to use a promo code, but the promo code has reached
 * its' maximum number of uses
 * @author kristiansuhartono
 *
 */
public class PromoCodeUsedUpException extends Exception {

	public PromoCodeUsedUpException() {
		super();
	}

	public PromoCodeUsedUpException(String message) {
		super(message);
	}

	public PromoCodeUsedUpException(String message, Throwable cause) {
		super(message, cause);
	}

	public PromoCodeUsedUpException(Throwable cause) {
		super(cause);
	}

}
