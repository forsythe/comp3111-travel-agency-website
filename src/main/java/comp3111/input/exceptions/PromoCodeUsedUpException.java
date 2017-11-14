package comp3111.input.exceptions;

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
