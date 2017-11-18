package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.ui.AbstractTextField;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

/**
 * Validates an integer value with a user inputted lower bound from a textfield
 * 
 * @author kristiansuhartono
 * @version 1.0
 * @since 2017-11-18
 */
public class IntegerLowerBoundedByAnotherFieldValidator implements Validator<Integer> {
	private AbstractTextField field;

	/**
	 * The constructor of the validator
	 * @param field The textfield that has the lower bound value
	 */
	public IntegerLowerBoundedByAnotherFieldValidator(AbstractTextField field) {
		this.field = field;
	}

	/** 
	 * Overrides the apply method in vaadin validators, checks whether the value is larger than the
	 * lower bound
	 * 
	 * @param value The integer value that is going to be validated.
	 * @param context A value context for converters. Contains relevant information for converting values. 
	 * @see com.vaadin.data.Validator#apply(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public ValidationResult apply(Integer value, ValueContext context) {
		try {
			int boundedBy = Integer.parseInt(field.getValue().replaceAll(",", ""));
			try {
				if (value >= boundedBy)
					return ValidationResult.ok();
			} catch (NumberFormatException e) {
				return getValidationErrorLogged("must be an integer");
			}
		} catch (NumberFormatException e) {
			// The lower bound itself is not valid. None of my business here :)
			return ValidationResult.ok();
		}
		return getValidationErrorLogged("the integer must be >= " + field.getCaption());

	}
}
