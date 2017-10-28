package comp3111.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

import comp3111.data.model.Offering;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class OfferingStillOpenValidator implements Validator<Offering> {

	OfferingStillOpenValidator() {
	}

	@Override
	public ValidationResult apply(Offering value, ValueContext context) {
		if (value != null){
			if (value.getLastEditableDate().before(Date.from(Instant.now()))){
				return ValidationResult.error("Offering is no longer open for application");
			}else{
				return  ValidationResult.ok();
			}
		}else{
			return ValidationResult.error("Offering does not exist!");
		}
	}
}
