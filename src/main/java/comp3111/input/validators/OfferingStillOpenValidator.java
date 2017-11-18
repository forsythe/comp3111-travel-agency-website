package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import comp3111.data.model.Offering;

import java.time.Instant;
import java.util.Date;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

/**
 * A validator that checks whether an offering is still open for new applications (has it gone over the last
 * possible day of booking or not)
 * 
 * @author kristiansuhartono
 * @version 1.0
 * @since 2017-11-18
 *
 */
public class OfferingStillOpenValidator implements Validator<Offering> {

	/**
	 * Constructor of the validator 
	 */
	OfferingStillOpenValidator() {
	}

	/** 
	 * Overrides the apply method in vaadin validators, checks whether the offering is still open or not
	 * 
	 * @param value The offering object that is going to be checked
	 * @param context A value context for converters. Contains relevant information for converting values. 
	 * @see com.vaadin.data.Validator#apply(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public ValidationResult apply(Offering value, ValueContext context) {
		if (value != null) {
			if (value.getStatus().equals(Offering.STATUS_CANCELLED)
					|| value.getLastEditableDate().before(Date.from(Instant.now()))) {
				return getValidationErrorLogged("offering is no longer open for application");
			} else {
				return ValidationResult.ok();
			}
		} else {
			return getValidationErrorLogged("offering does not exist");
		}
	}
}
