package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import comp3111.Utils;
import comp3111.data.model.Tour;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

/**
 * A validator to check whether a date is available for use in a tour.
 * As in whether the date is legal for a certain to
 * 
 * @author Kristian Suhartono
 * @version 1.0
 * @since 2017-11-18
 */
public class DateAvailableInTourValidator implements Validator<Date> {

	private Tour tour;

	/**
	 * Constructor for the validator
	 * @param tour This is the tour object that is going to use for validation
	 */
	DateAvailableInTourValidator(Tour tour) {
		this.tour = tour;
	}


	/** 
	 * Overrides the apply method in vaadin validators, checks whether the given value
	 * is valid in the tour object that the validator has.
	 * 
	 * @param value The value that is going to be validated
	 * @param context A value context for converters. Contains relevant information for converting values. 
	 * @see com.vaadin.data.Validator#apply(java.lang.Object, com.vaadin.data.ValueContext)
	 */
	@Override
	public ValidationResult apply(Date value, ValueContext context) {

		// check that the day of week is correct
		Collection<Integer> supportedDaysOfWeek = tour.getAllowedDaysOfWeek();
		if (!supportedDaysOfWeek.isEmpty()) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(value);
			int startDateDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

			// Sunday: 1, Monday: 2, Tuesday: 3...
			if (!supportedDaysOfWeek.contains(startDateDayOfWeek)) {
				StringBuilder msgBuilder = new StringBuilder();
				msgBuilder.append("only the following days are supported: ");
				msgBuilder.append(Utils.integerCollectionToString(supportedDaysOfWeek));
				return getValidationErrorLogged(msgBuilder.toString());
			}
		}

		Collection<Date> supportedDates = tour.getAllowedDates();
		if (!supportedDates.isEmpty() && !supportedDates.contains(value)) {
			StringBuilder msgBuilder = new StringBuilder();
			msgBuilder.append("only the following dates are supported: ");

			msgBuilder.append(Utils.dateCollectionToString(supportedDates));
			return getValidationErrorLogged(msgBuilder.toString());
		}

		return ValidationResult.ok();
	}
}
