package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

/**
 * Validates whether an integer is within a range or not
 * 
 * @author kristiansuhartono
 * @version 1.0
 * @since 2017-11-18
 */
public class IntegerRangeValidator implements Validator<Integer> {
	private int min;
	private int max;

	/**
	 * Constructor for the validator
	 * @param minInclusive The lower bound value, inclusive
	 * @param maxExclusive The upper bound value, inclusive
	 */
	IntegerRangeValidator(int minInclusive, int maxExclusive) {
		this.min = minInclusive;
		this.max = maxExclusive;
	}

	/** 
	 * Overrides the apply method in vaadin validators, checks whether the value is within the range
	 * 
	 * @param value The integer value that is going to be validated.
	 * @param context A value context for converters. Contains relevant information for converting values. 
	 * @see com.vaadin.data.Validator#apply(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public ValidationResult apply(Integer value, ValueContext context) {
		if (value >= min && value < max)
			return ValidationResult.ok();

		return getValidationErrorLogged("the integer must be [" + min + ", " + max + ")");

	}
}
