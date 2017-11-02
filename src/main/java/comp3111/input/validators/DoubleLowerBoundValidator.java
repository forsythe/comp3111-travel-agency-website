package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

public class DoubleLowerBoundValidator implements Validator<Double> {
	private double min;

	public DoubleLowerBoundValidator(double minInclusive) {
		this.min = minInclusive;
	}

	@Override
	public ValidationResult apply(Double value, ValueContext context) {
		try {
			if (value >= min)
				return ValidationResult.ok();
		} catch (NumberFormatException e) {
			return ValidationResult.error("must be a number");
		}
		return ValidationResult.error("the number must be >= " + min);
	}
}
