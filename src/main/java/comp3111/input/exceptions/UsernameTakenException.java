package comp3111.input.exceptions;

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
