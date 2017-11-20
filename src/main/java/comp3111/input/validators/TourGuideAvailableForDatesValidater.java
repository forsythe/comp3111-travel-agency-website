package comp3111.input.validators;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.ui.DateField;
import comp3111.Utils;
import comp3111.data.DBManager;
import comp3111.data.model.Offering;
import comp3111.data.model.TourGuide;
import java.util.Calendar;
import java.util.Date;

import static comp3111.input.validators.ReturnValidationErrorWithLogging.getValidationErrorLogged;

/**
 * Validates whether a tour guide is available between a certain date range.
 * 
 * 
 * 
 * @author Forsythe
 *
 */
public class TourGuideAvailableForDatesValidater implements Validator<TourGuide> {

	private DateField startDateField;
	private int duration;
	private DBManager dbManager;
	private Offering ignoredOffering;

	/**
	 * @param startDateField
	 *            The start date range to check
	 * @param duration
	 *            How long the duration you want to check, starting from startDate
	 * @param dbManager
	 *            The database manager
	 * @param ignoredOffering
	 *            An offering to ignore when checking if any date collisions occur
	 *            for this tour guide. Useful when you're editing an offering, and
	 *            you want to scan whether there are any date collisions (but you
	 *            don't want the offering to collide with itself when saving the
	 *            updated version to DB). Can be null
	 */
	TourGuideAvailableForDatesValidater(DateField startDateField, int duration, DBManager dbManager,
			Offering ignoredOffering) {
		this.startDateField = startDateField;
		this.duration = duration;
		this.dbManager = dbManager;
		this.ignoredOffering = ignoredOffering;
	}

	/**
	 * Overrides the apply method in vaadin validators, checks whether said
	 * tourguide is available on the dates that the validator was constructed with.
	 * 
	 * @param value
	 *            The TourGuide object whose availability shall be checked
	 * @param context
	 *            A value context for converters. Contains relevant information for
	 *            converting values.
	 * @see com.vaadin.data.Validator#apply(java.lang.Object,
	 *      com.vaadin.data.ValueContext)
	 */
	@Override
	public ValidationResult apply(TourGuide value, ValueContext context) {
		try {
			Date startDate = Utils.localDateToDate(startDateField.getValue());

			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			cal.add(Calendar.DATE, duration);
			Date endDate = cal.getTime();
			if (ignoredOffering == null || ignoredOffering.getId() == null) {
				if (dbManager.isTourGuideAvailableBetweenDate(value, startDate, endDate)) {
					return ValidationResult.ok();
				} else {
					return getValidationErrorLogged("tour guide not available in given period");
				}
			} else {
				if (dbManager.isTourGuideAvailableBetweenDateExcludeOffering(value, startDate, endDate,
						ignoredOffering)) {
					return ValidationResult.ok();
				} else {
					return getValidationErrorLogged("tour guide not available in given period");
				}
			}
		} catch (NullPointerException e) {
			// Some values are null, just jump to ok
			return ValidationResult.ok();
		}
	}
}
