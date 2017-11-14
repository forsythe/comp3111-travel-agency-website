package comp3111.input.exceptions;

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
