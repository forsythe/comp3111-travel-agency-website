package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

/**
 * Validates whether a string is a valid phone number string or not
 * 
 * @author kristiansuhartono
 * @version 1.0
 * @since 2017-11-18
 */
public class PhoneNumberValidator implements Validator<String> {

	/**
	 * Constructor of the validator
	 */
	PhoneNumberValidator() {
	}

	
	/** 
	 * Overrides the apply method in vaadin validators, checks whether the string is a valid phone number
	 * 
	 * @param value The phone number string that is to be validated
	 * @param context A value context for converters. Contains relevant information for converting values. 
	 * @see com.vaadin.data.Validator#apply(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public ValidationResult apply(String value, ValueContext context) {

		try {
			if (value.contains("-")) {
				String[] parts = value.split("-");
				if (parts.length == 2) {
					for (String s : parts) {
						Integer.parseInt(s);
					}
				} else {
					return getValidationErrorLogged("wrong format");
				}
			} else {
				Integer.parseInt(value);
			}
		} catch (NumberFormatException e) {
			return getValidationErrorLogged("must be a number");
		}

		return ValidationResult.ok();
	}
}
