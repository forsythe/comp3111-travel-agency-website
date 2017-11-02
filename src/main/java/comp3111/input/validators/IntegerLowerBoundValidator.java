package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

public class IntegerLowerBoundValidator implements Validator<Integer> {
	private int min;

	public IntegerLowerBoundValidator(int minInclusive) {
		this.min = minInclusive;
	}

	@Override
	public ValidationResult apply(Integer value, ValueContext context) {
		try {
			if (value >= min)
				return ValidationResult.ok();
		} catch (NumberFormatException e) {
			return getValidationErrorLogged("must be an integer");
		}
		return getValidationErrorLogged("the integer must be >= " + min);

	}
}
