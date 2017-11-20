package unit;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import com.vaadin.data.ValueContext;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;

import comp3111.data.model.Offering;
import comp3111.input.validators.DateIsEarlierThanOfferingLastEditableDateValidator;
import comp3111.input.validators.DateNotEarlierThanValidator;
import comp3111.input.validators.DoubleLowerBoundValidator;
import comp3111.input.validators.DoubleRangeValidator;
import comp3111.input.validators.HKIDValidator;
import comp3111.input.validators.IntegerLowerBoundValidator;
import comp3111.input.validators.IntegerLowerBoundedByAnotherFieldValidator;
import comp3111.input.validators.IntegerRangeValidator;
import comp3111.input.validators.ListOfDatesValidator;
import comp3111.input.validators.OfferingStillOpenValidator;
import comp3111.input.validators.PhoneNumberValidator;
import comp3111.input.validators.StringDoesNotContainSubstringValidator;
import comp3111.input.validators.StringLengthCanNullValidator;
import comp3111.input.validators.StringLengthValidator;
import comp3111.input.validators.StringNotEqualsToIgnoreCaseValidator;
import comp3111.input.validators.ValidatorFactory;

public class ValidationTests {

	@Test
	public void testPhoneNumberValidation() {
		// OK
		PhoneNumberValidator validator = ValidatorFactory.getPhoneNumberValidator();
		assertTrue(!validator.apply("12345678", new ValueContext()).isError());
		assertTrue(!validator.apply("852-12345678", new ValueContext()).isError());

		// Fail
		assertTrue(validator.apply("852-", new ValueContext()).isError());
		assertTrue(validator.apply("-", new ValueContext()).isError());
		assertTrue(validator.apply("", new ValueContext()).isError());
	}

	@Test
	public void testHKIDValidation() {
		// OK
		HKIDValidator validator = ValidatorFactory.getHKIDValidator();
		assertTrue(!validator.apply("G123456(A)", new ValueContext()).isError());
		assertTrue(!validator.apply("L555555(0)", new ValueContext()).isError());
		assertTrue(!validator.apply("AB987654(2)", new ValueContext()).isError());
		assertTrue(!validator.apply("C123456(9)", new ValueContext()).isError());

		// Fail
		assertTrue(validator.apply("AY987654(A)", new ValueContext()).isError());
		assertTrue(validator.apply("(A)", new ValueContext()).isError());
		assertTrue(validator.apply("123534", new ValueContext()).isError());
		assertTrue(validator.apply("A123456", new ValueContext()).isError());
		assertTrue(validator.apply("CVV1232", new ValueContext()).isError());
	}

	@Test
	public void testDateIsEarlierThanOfferingLastEditableDateValidation() {
		ComboBox<Offering> comboBox = new ComboBox<>();
		DateIsEarlierThanOfferingLastEditableDateValidator validator = ValidatorFactory
				.getDateIsEarlierThanOfferingLastEditableDateValidator(comboBox);

		Offering o1 = new Offering();
		Offering o2 = new Offering();

		assertTrue(comboBox.isEmpty());
		Date now = new GregorianCalendar(2017, Calendar.DECEMBER, 5).getTime();
		assertTrue(validator.apply(now, new ValueContext()).isError());
		comboBox.setItems(o1, o2);
		comboBox.setValue(null);
		assertTrue(validator.apply(now, new ValueContext()).isError());

		o1.setStartDate(new GregorianCalendar(2017, Calendar.DECEMBER, 4).getTime());
		o2.setStartDate(new GregorianCalendar(2017, Calendar.DECEMBER, 9).getTime());

		comboBox.setSelectedItem(o1);
		assertTrue(validator.apply(null, new ValueContext()).isError());
		assertTrue(validator.apply(now, new ValueContext()).isError());
		comboBox.setSelectedItem(o2);
		// OK
		assertTrue(!validator.apply(now, new ValueContext()).isError());
	}

	@Test
	public void testDateNotEarlierThanValidator() {
		Date now = new GregorianCalendar(2017, Calendar.DECEMBER, 5).getTime();
		Date tmr = new GregorianCalendar(2017, Calendar.DECEMBER, 6).getTime();
		Date yet = new GregorianCalendar(2017, Calendar.DECEMBER, 4).getTime();

		DateNotEarlierThanValidator validator = ValidatorFactory.getDateNotEarlierThanValidator(now);
		// Fail
		assertTrue(validator.apply(null, new ValueContext()).isError());
		assertTrue(validator.apply(yet, new ValueContext()).isError());
		// OK
		assertTrue(!validator.apply(tmr, new ValueContext()).isError());
	}

	@Test
	public void testDoubleRangeValidator() {
		DoubleRangeValidator validator = ValidatorFactory.getDoubleRangeValidator(1.0, 2.0);
		DoubleLowerBoundValidator validatorLower = ValidatorFactory.getDoubleRangeValidator(1.0);

		// Fail
		assertTrue(validator.apply(0.5, new ValueContext()).isError());
		assertTrue(validator.apply(2.5, new ValueContext()).isError());
		assertTrue(validatorLower.apply(0.5, new ValueContext()).isError());

		// OK
		assertTrue(!validator.apply(1.5, new ValueContext()).isError());
		assertTrue(!validatorLower.apply(1.5, new ValueContext()).isError());
	}

	@Test
	public void testIntegerLowerBoundedByAnotherFieldValidator() {
		TextField field = new TextField();
		IntegerLowerBoundedByAnotherFieldValidator validator = ValidatorFactory
				.getIntegerLowerBoundedByAnotherFieldValidator(field);

		// fail
		field.setValue("2");
		assertTrue(validator.apply(1, new ValueContext()).isError());

		// OK
		assertTrue(!validator.apply(3, new ValueContext()).isError());
		field.setValue("a");
		// still ok
		assertTrue(!validator.apply(1, new ValueContext()).isError());
	}

	@Test
	public void testIntegerRangeValidator() {
		IntegerRangeValidator validator = ValidatorFactory.getIntegerRangeValidator(1, 5);
		IntegerLowerBoundValidator validatorLower = ValidatorFactory.getIntegerRangeValidator(2);

		// Fail
		assertTrue(validator.apply(0, new ValueContext()).isError());
		assertTrue(validator.apply(7, new ValueContext()).isError());
		assertTrue(validatorLower.apply(1, new ValueContext()).isError());

		// OK
		assertTrue(!validator.apply(4, new ValueContext()).isError());
		assertTrue(!validatorLower.apply(3, new ValueContext()).isError());
	}

	@Test
	public void testListOfDatesValidator() {
		ListOfDatesValidator validator = ValidatorFactory.getListOfDatesValidator();

		// Fail
		assertTrue(validator.apply("a", new ValueContext()).isError());

		// OK
		assertTrue(!validator.apply(null, new ValueContext()).isError());
		assertTrue(!validator.apply("", new ValueContext()).isError());
		assertTrue(!validator.apply("11/11/2017", new ValueContext()).isError());
	}

	@Test
	public void testOfferingStillOpenValidator() {
		OfferingStillOpenValidator validator = ValidatorFactory.getOfferingStillOpenValidator();

		// Fail
		Offering o = new Offering();
		o.setStatus(Offering.STATUS_PENDING);
		o.setStartDate(new GregorianCalendar(2015, Calendar.DECEMBER, 4).getTime());
		assertTrue(validator.apply(null, new ValueContext()).isError());
		assertTrue(validator.apply(o, new ValueContext()).isError());

		o.setStatus(Offering.STATUS_CANCELLED);
		o.setStartDate(Date.from(Instant.now().plusSeconds(345600)));
		assertTrue(validator.apply(o, new ValueContext()).isError());

		// OK
		o.setStatus(Offering.STATUS_PENDING);
		o.setStartDate(Date.from(Instant.now().plusSeconds(345600)));
		assertTrue(!validator.apply(o, new ValueContext()).isError());
	}

	@Test
	public void testStringLengthValidator() {
		StringLengthCanNullValidator validatorCanNull = ValidatorFactory.getStringLengthCanNullValidator(5);
		StringLengthValidator validator = ValidatorFactory.getStringLengthValidator(5);

		// Fail
		assertTrue(validator.apply(null, new ValueContext()).isError());
		assertTrue(validator.apply("123456", new ValueContext()).isError());
		assertTrue(validatorCanNull.apply("123456", new ValueContext()).isError());

		// OK
		assertTrue(!validator.apply("123", new ValueContext()).isError());
		assertTrue(!validatorCanNull.apply("123", new ValueContext()).isError());
		assertTrue(!validatorCanNull.apply(null, new ValueContext()).isError());
	}

	@Test
	public void testStringNotEqualsToIgnoreCaseValidator() {
		StringNotEqualsToIgnoreCaseValidator validator = ValidatorFactory
				.getStringNotEqualsToIgnoreCaseValidator("bobby");

		// Fail
		assertTrue(validator.apply("bobby", new ValueContext()).isError());
		assertTrue(validator.apply("BoBBy", new ValueContext()).isError());

		// OK
		assertTrue(!validator.apply(null, new ValueContext()).isError());
		assertTrue(!validator.apply("bo-bboy", new ValueContext()).isError());
		assertTrue(!validator.apply("bobbyTables", new ValueContext()).isError());

	}

	@Test
	public void testStringDoesNotContainSubstringValidator() {
		StringDoesNotContainSubstringValidator validator = ValidatorFactory
				.getStringDoesNotContainSubstringValidator("bob");

		// Fail
		assertTrue(validator.apply("bobby", new ValueContext()).isError());
		assertTrue(validator.apply("bobbobby", new ValueContext()).isError());

		// OK
		assertFalse(validator.apply(null, new ValueContext()).isError());
		assertFalse(validator.apply("bo", new ValueContext()).isError());
		assertFalse(validator.apply("BoB", new ValueContext()).isError());

	}

}
