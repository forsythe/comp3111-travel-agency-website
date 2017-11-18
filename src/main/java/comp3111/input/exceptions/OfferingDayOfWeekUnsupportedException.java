package comp3111.input.exceptions;

/**
 * An exception that is thrown when someone is trying to create an Offering on days of week that is not 
 * offered by it's host tour (of the repeated tour type)
 * 
 * @author kristiansuhartono
 *
 */
public class OfferingDayOfWeekUnsupportedException extends Exception {

	public OfferingDayOfWeekUnsupportedException() {
		super();
	}

	public OfferingDayOfWeekUnsupportedException(String message) {
		super(message);
	}

	public OfferingDayOfWeekUnsupportedException(String message, Throwable cause) {
		super(message, cause);
	}

	public OfferingDayOfWeekUnsupportedException(Throwable cause) {
		super(cause);
	}

}
