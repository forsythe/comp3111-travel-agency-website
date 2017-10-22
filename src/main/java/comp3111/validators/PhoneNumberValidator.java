package comp3111.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

public class PhoneNumberValidator implements Validator<String> {

	public PhoneNumberValidator() {
	}

	@Override
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public ValidationResult apply(String value, ValueContext context) {

		try {
			if (value.contains("-")) {
				String[] parts = value.split("-");
				if (parts.length == 2) {
					for (String s : parts) {
						Integer.parseInt(s);
					}
				}else{
					return ValidationResult.error("Wrong Format");
				}
			}else{
				Integer.parseInt(value);
			}
		}catch (NumberFormatException e){
			return ValidationResult.error("Must be a number");
		}

		return ValidationResult.ok();
	}
}
