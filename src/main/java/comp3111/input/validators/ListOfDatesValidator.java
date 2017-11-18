package comp3111.input.validators;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

import comp3111.Utils;

/**
 * Validates Tour creation, for a limited tour type, it needs a number of dates. This validator
 * validates these dates, whether the dates are in the correct format or not, and also whether there is at
 * least one date
 * 
 * @author kristiansuhartono
 * @version 1.0
 * @since 2017-11-18
 *
 */
public class ListOfDatesValidator implements Validator<String> {

	/**
	 * Constructor of the validator
	 */
	public ListOfDatesValidator() {
	}

	/** 
	 * Overrides the apply method in vaadin validators, checks whether the list of dates is valid or not
	 * 
	 * @param listOfDates A string of the list of dates to be validated.
	 * @param context A value context for converters. Contains relevant information for converting values. 
	 * @see com.vaadin.data.Validator#apply(java.lang.Object, com.vaadin.data.ValueContext)
	 */
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
