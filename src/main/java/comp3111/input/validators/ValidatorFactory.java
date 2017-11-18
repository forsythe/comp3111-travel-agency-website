package comp3111.input.validators;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;

import comp3111.data.DBManager;
import comp3111.data.model.Offering;
import comp3111.data.model.Tour;

public class ValidatorFactory {
	private static final Logger log = LoggerFactory.getLogger(ValidatorFactory.class);

	public static IntegerRangeValidator getIntegerRangeValidator(int minInclusive, int maxExclusive) {
		return new IntegerRangeValidator(minInclusive, maxExclusive);
	}

	public static IntegerLowerBoundValidator getIntegerRangeValidator(int minInclusive) {
		return new IntegerLowerBoundValidator(minInclusive);
	}

	public static IntegerLowerBoundedByAnotherFieldValidator getIntegerLowerBoundedByAnotherFieldValidator(
			AbstractTextField field) {
		return new IntegerLowerBoundedByAnotherFieldValidator(field);
	}

	public static DoubleRangeValidator getDoubleRangeValidator(double minInclusive, double maxInclusive) {
		return new DoubleRangeValidator(minInclusive, maxInclusive);
	}

	public static DoubleLowerBoundValidator getDoubleRangeValidator(double minInclusive) {
		return new DoubleLowerBoundValidator(minInclusive);
	}

	public static ListOfDatesValidator getListOfDatesValidator() {
		return new ListOfDatesValidator();
	}

	public static StringLengthValidator getStringLengthValidator(int maxLength) {
		return new StringLengthValidator(maxLength);
	}

	public static StringLengthCanNullValidator getStringLengthCanNullValidator(int maxLength) {
		return new StringLengthCanNullValidator(maxLength);
	}

	public static PhoneNumberValidator getPhoneNumberValidator() {
		return new PhoneNumberValidator();
	}

	public static HKIDValidator getHKIDValidator() {
		return new HKIDValidator();
	}

	public static DateNotEarlierThanValidator getDateNotEarlierThanValidator(Date notEarlierThanThis) {
		return new DateNotEarlierThanValidator(notEarlierThanThis);
	}

	/**
	 * @param offering
	 *            need to pass in the offering combobox because the value could
	 *            change
	 * @return a DateIsEarlierThanOfferingLastEditableDateValidator object
	 */
	public static DateIsEarlierThanOfferingLastEditableDateValidator getDateIsEarlierThanOfferingLastEditableDateValidator(
			ComboBox<Offering> offering) {
		return new DateIsEarlierThanOfferingLastEditableDateValidator(offering);
	}

	public static OfferingStillOpenValidator getOfferingStillOpenValidator() {
		return new OfferingStillOpenValidator();
	}

	public static DateAvailableInTourValidator getDateAvailableInTourValidator(Tour tour) {
		return new DateAvailableInTourValidator(tour);
	}

	/**
	 * @param startDateField
	 *            the starting date to check
	 * @param duration
	 *            the length of the interval to check (in days)
	 * @param dbManager
	 *            A DBManager object
	 * @param ignoredOffering
	 *            the offering to ignore (can be null)
	 * @return whether or not the tour guide is available from time startDateField
	 *         to startDateField+duration. If ignoredOffering is not null, then we
	 *         ignore the time interval used by ignoredOffering when checking if
	 *         tour guide is free. Useful for when editing offerings, we don't want
	 *         the edited offering time-colliding with itself
	 */
	public static TourGuideAvailableForDatesValidation getTourGuideAvailableForDatesValidationIgnoreOneOffering(
			DateField startDateField, int duration, DBManager dbManager, Offering ignoredOffering) {
		return new TourGuideAvailableForDatesValidation(startDateField, duration, dbManager, ignoredOffering);
	}

	/**
	 * @param validationStatus
	 *            the validationStatus object after we fill in a vaadin form that's
	 *            binded to a bean
	 * 
	 * @return a string that we can display as an error notification
	 */
	public static <T> String getValidatorErrorsString(BinderValidationStatus<T> validationStatus) {
		StringBuilder sb = new StringBuilder();
		for (BindingValidationStatus<?> result : validationStatus.getFieldValidationErrors()) {
			// for TextFields and dates etc
			if (result.getField() instanceof AbstractComponent && result.getMessage().isPresent()) {
				sb.append("[" + ((AbstractComponent) result.getField()).getCaption() + "]").append(" ")
						.append(result.getMessage().get()).append("\n");
			}
		}
		return sb.toString();
	}
}
