package comp3111.exceptions;

public class ColumnNameNotFoundException extends Exception {

	public ColumnNameNotFoundException() {
		super();
	}

	public ColumnNameNotFoundException(String message) {
		super(message);
	}

	public ColumnNameNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ColumnNameNotFoundException(Throwable cause) {
		super(cause);
	}

}
