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

public class DateAvailableInTourValidator implements Validator<Date> {

	private Tour tour;
	DateAvailableInTourValidator(Tour tour) {
		this.tour = tour;
	}

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
				msgBuilder.append("Error! only the following days are supported");
				for (Integer i : supportedDaysOfWeek) {
					msgBuilder.append(String.format("\t%s", Utils.dayToString(i)));
				}
				return getValidationErrorLogged(msgBuilder.toString());
			}
		}

		Collection<Date> supportedDates = tour.getAllowedDates();
		if (!supportedDates.isEmpty() && !supportedDates.contains(value)) {
			StringBuilder msgBuilder = new StringBuilder();
			msgBuilder.append("Error! only the following dates are supported");
			for (Date d : supportedDates) {
				msgBuilder.append(String.format("\t%s", d));
			}
			return getValidationErrorLogged(msgBuilder.toString());
		}

		return ValidationResult.ok();
	}
}
