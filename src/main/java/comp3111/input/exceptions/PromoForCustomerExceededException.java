package comp3111.input.exceptions;

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
