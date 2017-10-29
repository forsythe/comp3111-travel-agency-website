package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.ui.AbstractTextField;

public class IntegerLowerBoundedByAnotherFieldValidator implements Validator<String> {
	private AbstractTextField field;

	public IntegerLowerBoundedByAnotherFieldValidator(AbstractTextField field) {
		this.field = field;
	}

	@Override
	public ValidationResult apply(String value, ValueContext context) {
		try {
			int boundedBy = Integer.parseInt(field.getValue());
			try {
				int target = Integer.parseInt(value);
				if (target >= boundedBy)
					return ValidationResult.ok();
			}catch (NumberFormatException e){
				return ValidationResult.error("Must be an integer");
			}
		} catch (NumberFormatException e) {
			//The lower bound itself is not valid. None of my business here :)
			return ValidationResult.ok();
		}
		return ValidationResult.error("The integer must be >= " + field.getCaption());

	}
}
