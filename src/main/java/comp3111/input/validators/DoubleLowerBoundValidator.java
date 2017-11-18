package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

public class DoubleLowerBoundValidator implements Validator<Double> {
	private double min;

	DoubleLowerBoundValidator(double minInclusive) {
		this.min = minInclusive;
	}

	@Override
	public ValidationResult apply(Double value, ValueContext context) {
		if (value >= min)
			return ValidationResult.ok();
		return ValidationResult.error("the number must be >= " + min);
	}
}
