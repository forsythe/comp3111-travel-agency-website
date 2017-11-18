package comp3111.input.exceptions;

/**
 * An exception that is thrown when a new login user has a same username as another user in the database
 * @author kristiansuhartono
 *
 */
public class UsernameTakenException extends Exception {

	public UsernameTakenException() {
		super();
	}

	public UsernameTakenException(String message) {
		super(message);
	}

	public UsernameTakenException(String message, Throwable cause) {
		super(message, cause);
	}

	public UsernameTakenException(Throwable cause) {
		super(cause);
	}

}
