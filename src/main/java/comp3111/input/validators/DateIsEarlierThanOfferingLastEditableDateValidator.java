package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.ui.ComboBox;

import comp3111.Utils;
import comp3111.data.model.Offering;

import java.util.Date;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

/**
 * Validates whether the date is earlier than an offering's last editable date, as in when creating
 * an offering, it validates whether the date is allowed or not
 * 
 * @author kristiansuhartono
 * @version 1.0
 * @since 2017-11-18
 *
 */
public class DateIsEarlierThanOfferingLastEditableDateValidator implements Validator<Date> {

	private ComboBox<Offering> o;

	/**
	 * The constructor of the validator
	 * @param offering The offering object that is going to be used for validation
	 */
	DateIsEarlierThanOfferingLastEditableDateValidator(ComboBox<Offering> offering) {
		this.o = offering;
	}

	/** 
	 * Overrides the apply method in vaadin validators, checks whether the given value
	 * is valid in the offering object that the validator has.
	 * @param value The date value that is going to be validated
	 * @param context A value context for converters. Contains relevant information for converting values. 
	 * @see com.vaadin.data.Validator#apply(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public ValidationResult apply(Date value, ValueContext context) {
		if (o.isEmpty() || o.getValue() == null) {
			return getValidationErrorLogged("there is no associated offering yet");
		}
		if (value != null) {
			if (!value.before(o.getValue().getLastEditableDate())) {
				return getValidationErrorLogged(
						"date must be earlier than " + Utils.simpleDateFormat(o.getValue().getLastEditableDate()));
			} else {
				return ValidationResult.ok();
			}
		} else {
			return getValidationErrorLogged("No associated date value");
		}
	}
}
