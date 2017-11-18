package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

public class IntegerRangeValidator implements Validator<Integer> {
	private int min;
	private int max;

	IntegerRangeValidator(int minInclusive, int maxExclusive) {
		this.min = minInclusive;
		this.max = maxExclusive;
	}

	@Override
	public ValidationResult apply(Integer value, ValueContext context) {
		if (value >= min && value < max)
			return ValidationResult.ok();

		return getValidationErrorLogged("the integer must be [" + min + ", " + max + ")");

	}
}
