package comp3111.input.converters;

import com.vaadin.data.converter.StringToBooleanConverter;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToIntegerConverter;

/**
 * A factory which generates converters
 * 
 * @author Forsythe
 */
public class ConverterFactory {
	/**
	 * @return A string to double converter
	 */
	public static StringToDoubleConverter getStringToDoubleConverter() {
		return new StringToDoubleConverter("must be a double");
	}

	/**
	 * @return A string to integer converter
	 */
	public static StringToIntegerConverter getStringToIntegerConverter() {
		return new StringToIntegerConverter("must be an integer");
	}

	/**
	 * @return A string to boolean converter
	 */
	public static StringToBooleanConverter getStringToBooleanConverter() {
		return new StringToBooleanConverter("must be true or false");
	}

	/**
	 * @return A LocalDateTime to UtilDateTime converter
	 */
	public static LocalDateTimeToUtilDateTimeConverter getLocalDateTimeToUtilDateTimeConverter() {
		return new LocalDateTimeToUtilDateTimeConverter();
	}

	public static LocalDateToUtilDateConverter getLocalDateToUtilDateConverter(){
		return new LocalDateToUtilDateConverter();
	}
}
