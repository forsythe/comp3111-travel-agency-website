package unit;

import com.vaadin.data.ValueContext;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import comp3111.Application;
import comp3111.data.model.Offering;
import comp3111.input.validators.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ValidationTests {

    @Test
    public void testPhoneNumberValidation (){
        //OK
        PhoneNumberValidator validator = ValidatorFactory.getPhoneNumberValidator();
        assert (!validator.apply("12345678", new ValueContext()).isError());
        assert (!validator.apply("852-12345678", new ValueContext()).isError());

        //Fail
        assert (validator.apply("852-", new ValueContext()).isError());
        assert (validator.apply("-", new ValueContext()).isError());
        assert (validator.apply("", new ValueContext()).isError());
    }

    @Test
    public void testHKIDValidation (){
        //OK
        HKIDValidator validator = ValidatorFactory.getHKIDValidator();
        assert (!validator.apply("G123456(A)", new ValueContext()).isError());
        assert (!validator.apply("L555555(0)", new ValueContext()).isError());
        assert (!validator.apply("AB987654(2)", new ValueContext()).isError());
        assert (!validator.apply("C123456(9)", new ValueContext()).isError());

        //Fail
        assert (validator.apply("AY987654(A)", new ValueContext()).isError());
        assert (validator.apply("(A)", new ValueContext()).isError());
        assert (validator.apply("123534", new ValueContext()).isError());
        assert (validator.apply("A123456", new ValueContext()).isError());
        assert (validator.apply("CVV1232", new ValueContext()).isError());
    }

    @Test
    public void testDateIsEarlierThanOfferingLastEditableDateValidation(){
        ComboBox<Offering> comboBox = new ComboBox<>();
        DateIsEarlierThanOfferingLastEditableDateValidator validator =
                ValidatorFactory.getDateIsEarlierThanOfferingLastEditableDateValidator(comboBox);

        Offering o1 = new Offering();
        Offering o2 = new Offering();

        assert (comboBox.isEmpty());
        Date now = new GregorianCalendar(2017, Calendar.DECEMBER, 5).getTime();
        assert(validator.apply(now, new ValueContext()).isError());
        comboBox.setItems(o1, o2);
        comboBox.setValue(null);
        assert(validator.apply(now, new ValueContext()).isError());

        o1.setStartDate(new GregorianCalendar(2017, Calendar.DECEMBER, 4).getTime());
        o2.setStartDate(new GregorianCalendar(2017, Calendar.DECEMBER, 9).getTime());

        comboBox.setSelectedItem(o1);
        assert(validator.apply(null, new ValueContext()).isError());
        assert(validator.apply(now, new ValueContext()).isError());
        comboBox.setSelectedItem(o2);
        //OK
        assert(!validator.apply(now, new ValueContext()).isError());
    }

    @Test
    public void testDateNotEarlierThanValidator(){
        Date now = new GregorianCalendar(2017, Calendar.DECEMBER, 5).getTime();
        Date tmr = new GregorianCalendar(2017, Calendar.DECEMBER, 6).getTime();
        Date yet = new GregorianCalendar(2017, Calendar.DECEMBER, 4).getTime();

        DateNotEarlierThanValidator validator = ValidatorFactory.getDateNotEarlierThanValidator(now);
        //Fail
        assert(validator.apply(null, new ValueContext()).isError());
        assert(validator.apply(yet, new ValueContext()).isError());
        //OK
        assert(!validator.apply(tmr, new ValueContext()).isError());
    }

    @Test
    public void testDoubleRangeValidator(){
        DoubleRangeValidator validator = ValidatorFactory.getDoubleRangeValidator(1.0, 2.0);
        DoubleLowerBoundValidator validatorLower = ValidatorFactory.getDoubleRangeValidator(1.0);

        //Fail
        assert (validator.apply(0.5, new ValueContext()).isError());
        assert (validator.apply(2.5, new ValueContext()).isError());
        assert (validatorLower.apply(0.5, new ValueContext()).isError());

        //OK
        assert (!validator.apply(1.5, new ValueContext()).isError());
        assert (!validatorLower.apply(1.5, new ValueContext()).isError());
    }

    @Test
    public void testIntegerLowerBoundedByAnotherFieldValidator(){
        TextField field = new TextField();
        IntegerLowerBoundedByAnotherFieldValidator validator = ValidatorFactory.getIntegerLowerBoundedByAnotherFieldValidator(field);

        //fail
        field.setValue("2");
        assert (validator.apply(1, new ValueContext()).isError());

        //OK
        assert (!validator.apply(3, new ValueContext()).isError());
        field.setValue("a");
        //still ok
        assert (!validator.apply(1, new ValueContext()).isError());
    }

    @Test
    public void testIntegerRangeValidator(){
        IntegerRangeValidator validator = ValidatorFactory.getIntegerRangeValidator(1, 5);
        IntegerLowerBoundValidator validatorLower = ValidatorFactory.getIntegerRangeValidator(2);

        //Fail
        assert (validator.apply(0, new ValueContext()).isError());
        assert (validator.apply(7, new ValueContext()).isError());
        assert (validatorLower.apply(1, new ValueContext()).isError());

        //OK
        assert (!validator.apply(4, new ValueContext()).isError());
        assert (!validatorLower.apply(3, new ValueContext()).isError());
    }

    @Test
    public void testListOfDatesValidator(){
        ListOfDatesValidator validator = ValidatorFactory.getListOfDatesValidator();

        //Fail
        assert (validator.apply("a", new ValueContext()).isError());

        //OK
        assert (!validator.apply(null, new ValueContext()).isError());
        assert (!validator.apply("", new ValueContext()).isError());
        assert (!validator.apply("11/11/2017", new ValueContext()).isError());
    }

    @Test
    public void testOfferingStillOpenValidator(){
        OfferingStillOpenValidator validator = ValidatorFactory.getOfferingStillOpenValidator();

        //Fail
        Offering o = new Offering();
        o.setStatus(Offering.STATUS_PENDING);
        o.setStartDate(new GregorianCalendar(2015, Calendar.DECEMBER, 4).getTime());
        assert (validator.apply(null, new ValueContext()).isError());
        assert (validator.apply(o, new ValueContext()).isError());

        o.setStatus(Offering.STATUS_CANCELLED);
        o.setStartDate(Date.from(Instant.now().plusSeconds(345600)));
        assert (validator.apply(o, new ValueContext()).isError());

        //OK
        o.setStatus(Offering.STATUS_PENDING);
        o.setStartDate(Date.from(Instant.now().plusSeconds(345600)));
        assert (!validator.apply(o, new ValueContext()).isError());
    }

    @Test
    public void testStringLengthValidator(){
        StringLengthCanNullValidator validatorCanNull = ValidatorFactory.getStringLengthCanNullValidator(5);
        StringLengthValidator validator = ValidatorFactory.getStringLengthValidator(5);

        //Fail
        assert (validator.apply(null, new ValueContext()).isError());
        assert (validator.apply("123456", new ValueContext()).isError());
        assert (validatorCanNull.apply("123456", new ValueContext()).isError());

        //OK
        assert (!validator.apply("123", new ValueContext()).isError());
        assert (!validatorCanNull.apply("123", new ValueContext()).isError());
        assert (!validatorCanNull.apply(null, new ValueContext()).isError());
    }
}
