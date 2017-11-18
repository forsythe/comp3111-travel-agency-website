package comp3111.input.validators;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

import comp3111.Utils;

public class ListOfDatesValidator implements Validator<String> {

	ListOfDatesValidator() {
	}

	@Override
	public ValidationResult apply(String listOfDates, ValueContext context) {
		if (listOfDates == null || listOfDates.isEmpty()) {
			return ValidationResult.ok();
		}
		try {
			String[] dates = listOfDates.replace(" ", "").split(",");

			SimpleDateFormat parser = new SimpleDateFormat(Utils.DATE_LOCALE);
			parser.setLenient(false);

			for (String s : dates) {
				parser.parse(s);
			}

		} catch (ParseException e) {
			return getValidationErrorLogged(
					"provide at least 1 date, " + Utils.DATE_LOCALE.toLowerCase() + ", separated by commas");
		}
		return ValidationResult.ok();

	}
}
