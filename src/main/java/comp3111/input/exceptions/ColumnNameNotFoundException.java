package comp3111.input.exceptions;

/**
 * An exception class that should be used when the column name is not found when it is being searched for. 
 * 
 * @author kristiansuhartono
 *
 */
public class ColumnNameNotFoundException extends Exception {

//	public ColumnNameNotFoundException() {
//		super();
//	}

	public ColumnNameNotFoundException(String message) {
		super(message);
	}

//	public ColumnNameNotFoundException(String message, Throwable cause) {
//		super(message, cause);
//	}
//
//	public ColumnNameNotFoundException(Throwable cause) {
//		super(cause);
//	}

}
