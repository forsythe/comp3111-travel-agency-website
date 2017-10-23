package comp3111.validators;

public class ValidatorFactory {

	public static IntegerRangeValidator getIntegerRangeValidator(int minInclusive, int maxExclusive) {
		return new IntegerRangeValidator(minInclusive, maxExclusive);
	}

	public static IntegerLowerBoundValidator getIntegerLowerBoundValidator(int minInclusive) {
		return new IntegerLowerBoundValidator(minInclusive);
	}

	public static DoubleRangeValidator getDoubleRangeValidator(double minInclusive, double maxInclusive) {
		return new DoubleRangeValidator(minInclusive, maxInclusive);
	}

	public static ListOfDatesValidator getListOfDatesValidator( ) {
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
}
