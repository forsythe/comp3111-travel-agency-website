package comp3111.input.exceptions;

/**
 * An exception that should be thrown when a customer tries to submit a promo code that does not exist.
 * 
 * @author kristiansuhartono
 *
 */
public class NoSuchPromoCodeException extends Exception {

	public NoSuchPromoCodeException() {
		super();
	}

	public NoSuchPromoCodeException(String message) {
		super(message);
	}

	public NoSuchPromoCodeException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSuchPromoCodeException(Throwable cause) {
		super(cause);
	}

}
