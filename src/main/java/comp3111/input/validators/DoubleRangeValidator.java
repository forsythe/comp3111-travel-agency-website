package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

public class DoubleRangeValidator implements Validator<Double> {
	private double min;
	private double max;

	public DoubleRangeValidator(double minInclusive, double maxInclusive) {
		this.min = minInclusive;
		this.max = maxInclusive;
	}

	@Override
	public ValidationResult apply(Double value, ValueContext context) {
		try {
			if (value >= min && value <= max)
				return ValidationResult.ok();
		} catch (NumberFormatException e) {
			return getValidationErrorLogged("Must be a number");
		}
		return getValidationErrorLogged("The number must be [" + min + ", " + max + "]");

	}
}
