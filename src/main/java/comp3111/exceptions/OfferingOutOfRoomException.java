package comp3111.exceptions;

public class OfferingOutOfRoomException extends Exception {

	public OfferingOutOfRoomException() {
		super();
	}

	public OfferingOutOfRoomException(String message) {
		super(message);
	}

	public OfferingOutOfRoomException(String message, Throwable cause) {
		super(message, cause);
	}

	public OfferingOutOfRoomException(Throwable cause) {
		super(cause);
	}

}
