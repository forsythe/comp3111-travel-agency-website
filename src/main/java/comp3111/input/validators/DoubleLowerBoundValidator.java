package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

public class DoubleLowerBoundValidator implements Validator<String> {
	private double min;

	public DoubleLowerBoundValidator(double minInclusive) {
		this.min = minInclusive;
	}

	@Override
	public ValidationResult apply(String value, ValueContext context) {
		try {
			double val = Double.parseDouble(value.replace(",", ""));
			if (val >= min)
				return ValidationResult.ok();
		} catch (NumberFormatException e) {
			return ValidationResult.error("Must be a number");
		}
		return ValidationResult.error("The number must be >= " + min);

	}
}
