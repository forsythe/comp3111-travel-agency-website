package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

/**
 * Validates whether a given HKID is valid or not
 * 
 * @author kristiansuhartono
 * @version 1.0
 * @since 2017-11-18
 *
 */
public class HKIDValidator implements Validator<String> {

	/**
	 * Constructor of the validator
	 */
	public HKIDValidator() {
	}

	/** 
	 * Overrides the apply method in vaadin validators, checks whether the HKID is legal by checking
	 * the check digit and the format of the HKID
	 * 
	 * @param value The HKID value that is going to be validated.
	 * @param context A value context for converters. Contains relevant information for converting values. 
	 * @see com.vaadin.data.Validator#apply(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public ValidationResult apply(String value, ValueContext context) {

		// A sample HKID is G123456(A)
		if (value.matches("[A-Z]{1,2}\\d{6}\\([0-9A]\\)")) {

			// Checking check digit
			String[] parts = value.split("\\(");
			int sum = 0;
			for (int i = parts[0].length(); i > 0; i--) {
				int val = Character.getNumericValue(parts[0].charAt(parts[0].length() - i));
				if (val >= 10) {
					val -= 9;
				}
				sum += val * (i + 1);
			}
			sum += Character.getNumericValue(parts[1].charAt(0));

			if (sum % 11 == 0) {
				return ValidationResult.ok();
			} else {
				return getValidationErrorLogged("wrong check digit");
			}
		}

		return getValidationErrorLogged("wrong format");
	}
}
