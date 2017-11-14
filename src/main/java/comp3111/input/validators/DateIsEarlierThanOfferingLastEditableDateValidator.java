package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.ui.ComboBox;

import comp3111.Utils;
import comp3111.data.model.Offering;

import java.util.Date;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

public class DateIsEarlierThanOfferingLastEditableDateValidator implements Validator<Date> {

	private ComboBox<Offering> o;

	DateIsEarlierThanOfferingLastEditableDateValidator(ComboBox<Offering> offering) {
		this.o = offering;
	}

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
