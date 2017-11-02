package comp3111.input.validators;

import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.DateField;
import comp3111.data.DBManager;
import comp3111.data.model.Tour;

import java.util.Date;

public class ValidatorFactory {

	public static IntegerRangeValidator getIntegerRangeValidator(int minInclusive, int maxExclusive) {
		return new IntegerRangeValidator(minInclusive, maxExclusive);
	}

	public static IntegerLowerBoundValidator getIntegerRangeValidator(int minInclusive) {
		return new IntegerLowerBoundValidator(minInclusive);
	}

	public static IntegerLowerBoundedByAnotherFieldValidator getIntegerLowerBoundedByAnotherFieldValidator
			(AbstractTextField field) {
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

	public static PhoneNumberValidator getPhoneNumberValidator() {
		return new PhoneNumberValidator();
	}

	public static HKIDValidator getHKIDValidator() {
		return new HKIDValidator();
	}

	public static DateNotEarlierThanValidator getDateNotEarlierThanValidator(Date notEarlierThanThis) {
		return new DateNotEarlierThanValidator(notEarlierThanThis);
	}

	public static OfferingStillOpenValidator getOfferingStillOpenValidator() {
		return new OfferingStillOpenValidator();
	}

	public static DateAvailableInTourValidator getDateAvailableInTourValidator(Tour tour){
		return new DateAvailableInTourValidator(tour);
	}

	public static TourGuideAvailableForDatesValidation getTourGuideAvailableForDatesValidation
			(DateField startDateField, int duration, DBManager dbManager){
		return new TourGuideAvailableForDatesValidation(startDateField, duration, dbManager);
	}
}
