package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

/**
 * Validates whether an integer value is larger than a lower bound or not
 * 
 * @author kristiansuhartono
 * @version 1.0
 * @since 2017-11-18
 */
public class IntegerLowerBoundValidator implements Validator<Integer> {
	private int min;

	/**
	 * The constructor of the validator
	 * 
	 * @param minInclusive
	 *            The lower bound integer value, inclusive
	 */
	IntegerLowerBoundValidator(int minInclusive) {
		this.min = minInclusive;
	}

	/**
	 * Overrides the apply method in vaadin validators, checks whether the value is
	 * larger than the lower bound
	 * 
	 * @param value
	 *            The integer value that is going to be validated.
	 * @param context
	 *            A value context for converters. Contains relevant information for
	 *            converting values.
	 * @see com.vaadin.data.Validator#apply(java.lang.Object,
	 *      com.vaadin.data.ValueContext)
	 */
	@Override
	public ValidationResult apply(Integer value, ValueContext context) {
		if (value >= min)
			return ValidationResult.ok();
		return getValidationErrorLogged("the integer must be >= " + min);

	}
}
