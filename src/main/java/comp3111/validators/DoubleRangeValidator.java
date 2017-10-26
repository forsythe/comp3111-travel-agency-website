package comp3111.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

public class DoubleRangeValidator implements Validator<String> {
	private double min;
	private double max;

	public DoubleRangeValidator(double minInclusive, double maxInclusive) {
		this.min = minInclusive;
		this.max = maxInclusive;
	}

	@Override
	public ValidationResult apply(String value, ValueContext context) {
		try {
			double val = Double.parseDouble(value);
			if (val >= min && val <= max)
				return ValidationResult.ok();
		} catch (NumberFormatException e) {
			return ValidationResult.error("Must be a number");
		}
		return ValidationResult.error("The number must be [" + min + ", " + max + "]");

	}
}
