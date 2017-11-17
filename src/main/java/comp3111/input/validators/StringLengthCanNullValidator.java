package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

/* Validates if a string is within a certain length, number inclusive*/
public class StringLengthCanNullValidator implements Validator<String> {
	private int maxLength;

	public StringLengthCanNullValidator(int maxLength) {
		this.maxLength = maxLength;
	}

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
