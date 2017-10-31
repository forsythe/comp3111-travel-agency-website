package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import comp3111.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

public class DateNotEarlierThanValidator implements Validator<Date> {

	private static final Logger log = LoggerFactory.getLogger(DateNotEarlierThanValidator.class);
	private Date notEarlierThanThis;

	DateNotEarlierThanValidator(Date notEarlierThanThis) {
		this.notEarlierThanThis = notEarlierThanThis;
	}

	@Override
	public ValidationResult apply(Date value, ValueContext context) {
		if (value != null) {
			if (value.before(notEarlierThanThis)) {
				return getValidationErrorLogged(
						"Date cannot be earlier than " + Utils.simpleDateFormat(notEarlierThanThis));
			} else {
				return ValidationResult.ok();
			}
		} else {
			return getValidationErrorLogged("Date does not exist!");
		}
	}
}
