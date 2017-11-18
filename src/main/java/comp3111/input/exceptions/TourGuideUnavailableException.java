package comp3111.input.exceptions;

/**
 * An exception that is thrown when the offering is trying to be created with a TourGuide that is 
 * already guiding a different offering at that specific time period.
 * @author kristiansuhartono
 *
 */
public class TourGuideUnavailableException extends Exception {

	public TourGuideUnavailableException() {
		super();
	}

//	public TourGuideUnavailableException(String message) {
//		super(message);
//	}
//
//	public TourGuideUnavailableException(String message, Throwable cause) {
//		super(message, cause);
//	}
//
//	public TourGuideUnavailableException(Throwable cause) {
//		super(cause);
//	}

}
