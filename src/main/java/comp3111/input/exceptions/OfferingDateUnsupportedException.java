package comp3111.input.exceptions;

/**
 * An exception that is thrown when someone is trying to create an Offering object with a date that is not
 * available or valid in it's host tour (of the limited tour type)
 * 
 * @author kristiansuhartono
 *
 */
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
