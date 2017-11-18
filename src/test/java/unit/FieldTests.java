package unit;

import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToBooleanConverter;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import comp3111.Application;
import comp3111.input.converters.*;
import comp3111.input.field.HKIDEntryField;
import comp3111.input.field.PhoneNumberEntryField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;

import static org.assertj.core.api.BDDAssertions.then;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class FieldTests {

    @Test
    public void testHKIDEntryField (){
        HKIDEntryField field = new HKIDEntryField();

        field.setValue(null);
        assert (field.isEmpty());
        field.setValue("1");
        assert (field.isEmpty());
        field.setValue("(");
        assert (field.isEmpty());

        field.setValue("G(1)");
        assert (field.getValue().equals("G(1)"));
    }

    @Test
    public void testPhoneEntryField (){
        PhoneNumberEntryField field = new PhoneNumberEntryField();

        field.setValue(null);
        assert (field.isEmpty());

        field.setValue("852-12345678");
        assert (field.getValue().equals("852-12345678"));

        field.setValue("12345678");
        System.err.println(field.getValue());
        then (field.getValue()).isEqualTo("12345678");
    }
}
