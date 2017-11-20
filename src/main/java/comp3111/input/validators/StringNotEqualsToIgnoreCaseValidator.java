package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

/**
 * Validates that a string is not equal to a specified string, ignoring case
 * 
 * @author Forsythe
 */
public class StringNotEqualsToIgnoreCaseValidator implements Validator<String> {
	private String banned;

	/**
	 * The constructor of the validator
	 * 
	 * @param bannedString
	 *            The string which the value being validated should not equal,
	 *            ignoring case
	 */
	public StringNotEqualsToIgnoreCaseValidator(String bannedString) {
		this.banned = bannedString;
	}

	/**
	 * Overrides the apply method in vaadin validators, checks whether the string is
	 * not equal to the specified string, ignoring case
	 * 
	 * @param value
	 *            The string whose value is being validated
	 * @param context
	 *            A value context for validators. Contains relevant information for
	 *            validating values.
	 * @see com.vaadin.data.Validator#apply(java.lang.Object,
	 *      com.vaadin.data.ValueContext)
	 */
	@Override
	public ValidationResult apply(String value, ValueContext context) {
		if (value == null || !value.equalsIgnoreCase(banned))
			return ValidationResult.ok();

		return getValidationErrorLogged("the string must not equal [" + banned + "], case insensitive");

	}
}
