package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import comp3111.data.model.Offering;

import java.time.Instant;
import java.util.Date;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

public class OfferingStillOpenValidator implements Validator<Offering> {

	OfferingStillOpenValidator() {
	}

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
