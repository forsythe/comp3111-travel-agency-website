package comp3111.input.exceptions;

/**
 * An exception that is thrown when customers are trying to book a place in an Offering, but 
 * there is no more space in the offering for the amount of people that the customer specifies 
 * 
 * @author kristiansuhartono
 *
 */
public class OfferingOutOfRoomException extends Exception {

	public OfferingOutOfRoomException() {
		super();
	}
//
//	public OfferingOutOfRoomException(String message) {
//		super(message);
//	}
//
//	public OfferingOutOfRoomException(String message, Throwable cause) {
//		super(message, cause);
//	}
//
//	public OfferingOutOfRoomException(Throwable cause) {
//		super(cause);
//	}

}
