package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

public class IntegerLowerBoundValidator implements Validator<Integer> {
	private int min;

	IntegerLowerBoundValidator(int minInclusive) {
		this.min = minInclusive;
	}

	@Override
	public ValidationResult apply(Integer value, ValueContext context) {
		if (value >= min)
			return ValidationResult.ok();
		return getValidationErrorLogged("the integer must be >= " + min);

	}
}
