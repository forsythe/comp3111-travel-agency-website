package comp3111.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

public class HKIDValidator implements Validator<String> {

	public HKIDValidator() {
	}

	@Override
	public ValidationResult apply(String value, ValueContext context) {

		//A sample HKID is G123456(A)
		if (value.matches("[A-Z]{1,2}\\d{6}\\([0-9A]\\)")){

			//Checking check digit
			String[] parts = value.split("\\(");
			int sum = 0;
			for (int i=parts[0].length(); i>0; i--){
				int val = Character.getNumericValue(parts[0].charAt(parts[0].length()-i));
				if (val >= 10){
					val -= 9;
				}
				sum += val * (i+1);
			}
			sum += Character.getNumericValue(parts[1].charAt(0));

			if (sum % 11 == 0) {
				return ValidationResult.ok();
			}else{
				return ValidationResult.error("Wrong check digit");
			}
		}

		return ValidationResult.error("Wrong format");
	}
}
