package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

/**
 * Validates whether a double value is higher than a lower bound value
 * 
 * @author kristiansuhartono
 * @version 1.0
 * @since 2017-11-18
 *
 */
public class DoubleLowerBoundValidator implements Validator<Double> {
	private double min;

	/**
	 * The constructor of the validator
	 * 
	 * @param minInclusive
	 *            The lower bound, inclusive.
	 */
	public DoubleLowerBoundValidator(double minInclusive) {
		this.min = minInclusive;
	}

	/**
	 * Overrides the apply method in vaadin validators, checks whether the given
	 * value is higher than the lower bound.
	 * 
	 * @param value
	 *            The double value that is going to be validated.
	 * @param context
	 *            A value context for converters. Contains relevant information for
	 *            converting values.
	 * @see com.vaadin.data.Validator#apply(java.lang.Object,
	 *      com.vaadin.data.ValueContext)
	 */
	@Override
	public ValidationResult apply(Double value, ValueContext context) {
		try {
			if (value >= min)
				return ValidationResult.ok();
		} catch (NumberFormatException e) {
			return ValidationResult.error("must be a number");
		}
		return ValidationResult.error("the number must be >= " + min);
	}
}
