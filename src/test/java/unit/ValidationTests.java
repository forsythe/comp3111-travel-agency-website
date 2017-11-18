package unit;

import static org.assertj.core.api.Assertions.assertThat;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.ValueContext;

import comp3111.input.validators.HKIDValidator;
import comp3111.input.validators.PhoneNumberValidator;
import comp3111.input.validators.ValidatorFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ValidationTests {

    @Test
    public void testPhoneNumberValidation (){
        PhoneNumberValidator validator = ValidatorFactory.getPhoneNumberValidator();

        assertThat (validator.apply("852-12345678", new ValueContext()) == ValidationResult.ok());
        assertThat (validator.apply("004-233563324", new ValueContext()) == ValidationResult.ok());
        assertThat (validator.apply("12345678", new ValueContext()) == ValidationResult.ok());

        assertThat (validator.apply("852-", new ValueContext()) != ValidationResult.ok());
        assertThat (validator.apply("-", new ValueContext()) != ValidationResult.ok());
        assertThat (validator.apply("", new ValueContext()) != ValidationResult.ok());
        assertThat (validator.apply("---", new ValueContext()) != ValidationResult.ok());
        assertThat (validator.apply("4-5-6-4-3", new ValueContext()) != ValidationResult.ok());
        assertThat (validator.apply("- -", new ValueContext()) != ValidationResult.ok());
    }

    @Test
    public void testHKIDValidation (){
        HKIDValidator validator = ValidatorFactory.getHKIDValidator();

        assertThat (validator.apply("G123456(A)", new ValueContext()) == ValidationResult.ok());
        assertThat (validator.apply("L555555(0)", new ValueContext()) == ValidationResult.ok());
        assertThat (validator.apply("AB987654(2)", new ValueContext()) == ValidationResult.ok());
        assertThat (validator.apply("C123456(9)", new ValueContext()) == ValidationResult.ok());

        assertThat (validator.apply("AY987654(A)", new ValueContext()) != ValidationResult.ok());
        assertThat (validator.apply("(A)", new ValueContext()) != ValidationResult.ok());
        assertThat (validator.apply("123534", new ValueContext()) != ValidationResult.ok());
        assertThat (validator.apply("A123456", new ValueContext()) != ValidationResult.ok());
        assertThat (validator.apply("CVV1232", new ValueContext()) != ValidationResult.ok());

    }
}
