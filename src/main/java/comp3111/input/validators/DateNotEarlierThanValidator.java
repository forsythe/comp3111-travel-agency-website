package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import comp3111.Utils;

import java.util.Date;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

/**
 * Validates whether a date is earlier than another date.
 * 
 * @author kristiansuhartono
 * @version 1.0
 * @since 2017-11-18
 *
 */
public class DateNotEarlierThanValidator implements Validator<Date> {

	private Date notEarlierThanThis;

	/**
	 * Constructor for the validator
	 * 
	 * @param notEarlierThanThis The date that is to be compared to
	 */
	DateNotEarlierThanValidator(Date notEarlierThanThis) {
		this.notEarlierThanThis = notEarlierThanThis;
	}
	/** 
	 * Overrides the apply method in vaadin validators, checks whether the given value
	 * is earlier than the date object that the validator has
	 * @param value The date value that is going to be validated
	 * @param context A value context for converters. Contains relevant information for converting values. 
	 * @see com.vaadin.data.Validator#apply(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public ValidationResult apply(Date value, ValueContext context) {
		if (value != null) {
			if (value.before(notEarlierThanThis)) {
				return getValidationErrorLogged(
						"date must be later than " + Utils.simpleDateFormat(notEarlierThanThis));
			} else {
				return ValidationResult.ok();
			}
		} else {
			return getValidationErrorLogged("date does not exist");
		}
	}
}
