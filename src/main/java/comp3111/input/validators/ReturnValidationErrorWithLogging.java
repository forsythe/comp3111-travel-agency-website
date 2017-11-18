package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that logs all incidents of errors during validation
 * @author kristiansuhartono
 * @version 1.0
 * @since 2017-11-18
 *
 */
public class ReturnValidationErrorWithLogging {
	private static final Logger log = LoggerFactory.getLogger(ReturnValidationErrorWithLogging.class);

	/**
	 * The function that logs the error to the logger.
	 * @param msg The message that describes the error
	 * @return
	 */
	static ValidationResult getValidationErrorLogged(String msg) {
		log.info("Validation error: " + msg);
		return ValidationResult.error(msg);
	}

}
