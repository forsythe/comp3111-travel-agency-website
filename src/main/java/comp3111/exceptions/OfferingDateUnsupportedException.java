package comp3111.exceptions;

public class OfferingDateUnsupportedException extends Exception {

	public OfferingDateUnsupportedException() {
		super();
	}

	public OfferingDateUnsupportedException(String message) {
		super(message);
	}

	public OfferingDateUnsupportedException(String message, Throwable cause) {
		super(message, cause);
	}

	public OfferingDateUnsupportedException(Throwable cause) {
		super(cause);
	}

}
