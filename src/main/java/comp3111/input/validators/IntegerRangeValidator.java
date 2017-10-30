package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

public class IntegerRangeValidator implements Validator<String> {
	private int min;
	private int max;

	public IntegerRangeValidator(int minInclusive, int maxExclusive) {
		this.min = minInclusive;
		this.max = maxExclusive;
	}

	@Override
	public ValidationResult apply(String value, ValueContext context) {
		try {
			int val = Integer.parseInt(value.replace(",", ""));
			if (val >= min && val < max)
				return ValidationResult.ok();
		} catch (NumberFormatException e) {
			return ValidationResult.error("Must be an integer");
		}
		return ValidationResult.error("The integer must be [" + min + ", " + max + ")");

	}
}
