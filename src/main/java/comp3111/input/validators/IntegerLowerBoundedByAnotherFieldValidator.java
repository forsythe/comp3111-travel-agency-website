package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.ui.AbstractTextField;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

public class IntegerLowerBoundedByAnotherFieldValidator implements Validator<Integer> {
	private AbstractTextField field;

	public IntegerLowerBoundedByAnotherFieldValidator(AbstractTextField field) {
		this.field = field;
	}

	@Override
	public ValidationResult apply(Integer value, ValueContext context) {
		try {
			int boundedBy = Integer.parseInt(field.getValue().replaceAll(",", ""));
			try {
				if (value >= boundedBy)
					return ValidationResult.ok();
			} catch (NumberFormatException e) {
				return getValidationErrorLogged("Must be an integer");
			}
		} catch (NumberFormatException e) {
			// The lower bound itself is not valid. None of my business here :)
			return ValidationResult.ok();
		}
		return getValidationErrorLogged("The integer must be >= " + field.getCaption());

	}
}
