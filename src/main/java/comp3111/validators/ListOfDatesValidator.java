package comp3111.validators;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

public class ListOfDatesValidator implements Validator<String> {

	public ListOfDatesValidator() {
	}

	@Override
	public ValidationResult apply(String listOfDates, ValueContext context) {
		if (listOfDates == null || listOfDates.isEmpty()) {
			return ValidationResult.ok();
		}
		try {
			String[] dates = listOfDates.replace(" ", "").split(",");
	
			SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yy");
			parser.setLenient(false);

			for (String s : dates) {
				parser.parse(s);
			}

		} catch (ParseException e) {
			return ValidationResult.error("Provide at least 1 date, dd/mm/yyyy, separated by commas");
		}
		return ValidationResult.ok();

	}
}
