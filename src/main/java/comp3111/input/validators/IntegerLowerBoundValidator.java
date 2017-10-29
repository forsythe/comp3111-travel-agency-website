package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

public class IntegerLowerBoundValidator implements Validator<String> {
	private int min;

	public IntegerLowerBoundValidator(int minInclusive) {
		this.min = minInclusive;
	}

	@Override
	public ValidationResult apply(String value, ValueContext context) {
		try {
			int val = Integer.parseInt(value);
			if (val >= min)
				return ValidationResult.ok();
		} catch (NumberFormatException e) {
			return ValidationResult.error("Must be an integer");
		}
		return ValidationResult.error("The integer must be >= " + min);

	}
}
