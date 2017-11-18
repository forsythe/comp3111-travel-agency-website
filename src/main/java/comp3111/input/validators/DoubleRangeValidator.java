package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

/**
 * Validates whether a double value is within a range
 * 
 * @author kristiansuhartono
 * @version 1.0
 * @since 2017-11-18
 *
 */
public class DoubleRangeValidator implements Validator<Double> {
	private double min;
	private double max;

	/**
	 * Constructor of the validator
	 * @param minInclusive The lower bound, inclusive
	 * @param maxInclusive The upper bound, inclusive
	 */
	public DoubleRangeValidator(double minInclusive, double maxInclusive) {
		this.min = minInclusive;
		this.max = maxInclusive;
	}

	/** 
	 * Overrides the apply method in vaadin validators, checks whether the given value is within
	 * the range.
	 * @param value The double value that is going to be validated.
	 * @param context A value context for converters. Contains relevant information for converting values. 
	 * @see com.vaadin.data.Validator#apply(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public ValidationResult apply(Double value, ValueContext context) {
		try {
			if (value >= min && value <= max)
				return ValidationResult.ok();
		} catch (NumberFormatException e) {
			return getValidationErrorLogged("must be a number");
		}
		return getValidationErrorLogged("the number must be [" + min + ", " + max + "]");

	}
}
