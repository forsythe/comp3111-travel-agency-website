package comp3111.validators;

import java.util.IllegalFormatException;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

public class StringLengthValidator implements Validator<String> {
	private final int maxLength = 255;

	public StringLengthValidator() {
	}

	@Override
	public ValidationResult apply(String value, ValueContext context) {
		try {
			if(value.length() <= 255)
				return ValidationResult.ok();
		} catch (Exception e) {
			return ValidationResult.error("Something went wrong");
		}
		return ValidationResult.error("The string must be <= 255 characters");

	}
}
