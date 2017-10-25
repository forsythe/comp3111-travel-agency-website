package comp3111.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateNotEarlierThanValidator implements Validator<Date> {

	private Date notEarlierThanThis;
	DateNotEarlierThanValidator(Date notEarlierThanThis) {
		this.notEarlierThanThis = notEarlierThanThis;
	}

	@Override
	public ValidationResult apply(Date value, ValueContext context) {
		if (value != null){
			if (value.before(notEarlierThanThis)){
				return ValidationResult.error("Date cannot be earlier than " +
						new SimpleDateFormat("dd-MM-yyyy").format(notEarlierThanThis));
			}else{
				return  ValidationResult.ok();
			}
		}else{
			return ValidationResult.error("Date does not exist!");
		}
	}
}
