package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

/* Validates if a string is within a certain length, number inclusive*/
/**
 * Validates if a string is within a certain length, number inclusive
 * 
 * @author kristiansuhartono
 * @version 1.0
 * @since 2017-11-18
 */
public class StringLengthCanNullValidator implements Validator<String> {
	private int maxLength;

	/**
	 * The constructor of the validator
	 * @param maxLength The maximum length of the string
	 */
	public StringLengthCanNullValidator(int maxLength) {
		this.maxLength = maxLength;
	}

	/** 
	 * Overrides the apply method in vaadin validators, checks whether the string is of an appropriate length, but
	 * it allows the string to be null.
	 * 
	 * @param value The string whose length is to be validated.
	 * @param context A value context for converters. Contains relevant information for converting values. 
	 * @see com.vaadin.data.Validator#apply(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public ValidationResult apply(String value, ValueContext context) {
		try {
			if(value.length() <= maxLength)
				return ValidationResult.ok();
		} catch (NullPointerException e) {
			return ValidationResult.ok();
		}
		return getValidationErrorLogged("the string must be <= " + maxLength + " characters");

	}
}
