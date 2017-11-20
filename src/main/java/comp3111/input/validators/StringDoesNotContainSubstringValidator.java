package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

/**
 * Validates that a string does not contain a substring
 * 
 * @author Forsythe
 */
public class StringDoesNotContainSubstringValidator implements Validator<String> {
	private String bannedSubstr;

	/**
	 * The constructor of the validator
	 * 
	 * @param bannedSubstr
	 *            The substring which is not allowed
	 */
	public StringDoesNotContainSubstringValidator(String bannedSubstr) {
		this.bannedSubstr = bannedSubstr;
	}

	/**
	 * Overrides the apply method in vaadin validators, checks whether the the
	 * string contains the banned substring
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
		if (value == null || !value.contains(bannedSubstr))
			return ValidationResult.ok();

		return getValidationErrorLogged("the string must not contain \"" + bannedSubstr + "\"");

	}
}
