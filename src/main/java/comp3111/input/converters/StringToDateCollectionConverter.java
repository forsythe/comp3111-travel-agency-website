package comp3111.input.converters;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import comp3111.Utils;

import java.util.Collection;
import java.util.Date;

/**
 * Converts between a string a collection of dates
 * 
 * @author Forsythe
 *
 */
public class StringToDateCollectionConverter implements Converter<String, Collection<Date>> {

	@Override
	public Result<Collection<Date>> convertToModel(String value, ValueContext context) {
		// The try catch block shouldn't be needed as the validator should capture bad
		// inputs.
		try {
			return Result.ok(Utils.stringToDateSet(value));
		} catch (Exception e) {
			return Result.error("Please enter a proper date");
		}
	}

	@Override
	public String convertToPresentation(Collection<Date> value, ValueContext context) {
		return Utils.dateCollectionToString(value);
	}

	public Class<Collection> getModelType() {
		return Collection.class;
	}

	public Class<String> getPresentationType() {
		return String.class;
	}

}
