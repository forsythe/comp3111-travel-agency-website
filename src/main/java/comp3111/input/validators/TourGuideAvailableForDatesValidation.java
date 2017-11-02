package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.ui.DateField;
import comp3111.Utils;
import comp3111.data.DBManager;
import comp3111.data.model.TourGuide;
import comp3111.input.converters.LocalDateToUtilDateConverter;

import java.util.Calendar;
import java.util.Date;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

public class TourGuideAvailableForDatesValidation implements Validator<TourGuide> {

	private DateField startDateField;
	private int duration;
	private DBManager dbManager;

	TourGuideAvailableForDatesValidation(DateField startDateField, int duration, DBManager dbManager) {
		this.startDateField = startDateField;
		this.duration = duration;
		this.dbManager = dbManager;
	}

	@Override
	public ValidationResult apply(TourGuide value, ValueContext context) {
		try {
			Date startDate = Utils.localDateToDate(startDateField.getValue());

			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			cal.add(Calendar.DATE, duration);
			Date endDate = cal.getTime();

			if (dbManager.isTourGuideAvailableBetweenDate(value, startDate, endDate)) {
				return ValidationResult.ok();
			} else {
				return getValidationErrorLogged("tour guide not available in given period");
			}
		} catch (NullPointerException e) {
			// Some values are null, just jump to ok
			return ValidationResult.ok();
		}
	}
}
