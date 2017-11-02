package comp3111.input.converters;

import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToIntegerConverter;

public class ConverterFactory {
	public static StringToDoubleConverter getStringToDoubleConverter() {
		return new StringToDoubleConverter("must be a double");
	}

	public static StringToIntegerConverter getStringToIntegerConverter() {
		return new StringToIntegerConverter("must be an integer");
	}
}
