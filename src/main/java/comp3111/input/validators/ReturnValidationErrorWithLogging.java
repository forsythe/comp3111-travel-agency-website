package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReturnValidationErrorWithLogging {
    private static final Logger log = LoggerFactory.getLogger(ReturnValidationErrorWithLogging.class);

    static ValidationResult getValidationErrorLogged(String msg){
        log.info("Validation error: " + msg);
        return ValidationResult.error(msg);
    }
}
