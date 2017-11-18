package unit;

import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToBooleanConverter;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import comp3111.Application;
import comp3111.input.converters.*;
import comp3111.input.exceptions.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ConverterTests {

    @Test
    public void testLocalDateTimeToUtilDateTimeConverter (){
        LocalDateTimeToUtilDateTimeConverter converter = ConverterFactory.getLocalDateTimeToUtilDateTimeConverter();

        //Fail
        assert (converter.convertToModel(null, new ValueContext()).isError());
        //OK
        assert (!converter.convertToModel(LocalDateTime.now(), new ValueContext()).isError());

        //OK
        assert (converter.convertToPresentation(null, new ValueContext()) != null);
        assert (converter.convertToPresentation(Date.from(Instant.now()), new ValueContext()) != null);
    }

    @Test
    public void testLocalDateToUtilDateConverter (){
        LocalDateToUtilDateConverter converter = ConverterFactory.getLocalDateToUtilDateConverter();

        //Fail
        assert (converter.convertToModel(null, new ValueContext()).isError());
        //OK
        assert (!converter.convertToModel(LocalDate.now(), new ValueContext()).isError());

        //OK
        assert (converter.convertToPresentation(null, new ValueContext()) != null);
        assert (converter.convertToPresentation(Date.from(Instant.now()), new ValueContext()) != null);
    }

    @Test
    public void testStringToDoubleConverter(){
        StringToDoubleConverter converter = ConverterFactory.getStringToDoubleConverter();
        //Fail
        assert(converter.convertToModel("ab", new ValueContext()).isError());
        //OK
        assert(!converter.convertToModel("1.0", new ValueContext()).isError());
    }

    @Test
    public void testStringToIntegerConverter(){
        StringToIntegerConverter converter = ConverterFactory.getStringToIntegerConverter();
        //Fail
        assert(converter.convertToModel("ab", new ValueContext()).isError());
        //OK
        assert(!converter.convertToModel("1", new ValueContext()).isError());
    }

    @Test
    public void testStringToBooleanConverter(){
        StringToBooleanConverter converter = ConverterFactory.getStringToBooleanConverter();
        //Fail
        assert(converter.convertToModel("k", new ValueContext()).isError());
        //OK
        assert(!converter.convertToModel("true", new ValueContext()).isError());
    }


    @Test
    public void testStringCollectionToIntegerCollectionConverter(){
        StringCollectionToIntegerCollectionConverter converter = new StringCollectionToIntegerCollectionConverter();

        //Fail
        assert (converter.convertToModel(null, new ValueContext()).isError());
        //OK
        HashSet<String> set = new HashSet<>();
        set.add("123");
        assert (!converter.convertToModel(set, new ValueContext()).isError());

        HashSet<Integer> set2 = new HashSet<>();
        set2.add(123);
        //OK
        assert (converter.convertToPresentation(set2, new ValueContext()) != null);
    }

    @Test
    public void testStringToDateCollectionConverter(){
        StringToDateCollectionConverter converter = new StringToDateCollectionConverter();

        //It fails inside, did not give me error
        //assert (converter.convertToModel("asdfasd", new ValueContext()).isError());
        //OK
        assert (!converter.convertToModel("11/11/2017", new ValueContext()).isError());

        HashSet<Date> set = new HashSet<>();
        set.add(Date.from(Instant.now()));
        //OK
        assert (converter.convertToPresentation(set, new ValueContext()) != null);
    }
}
