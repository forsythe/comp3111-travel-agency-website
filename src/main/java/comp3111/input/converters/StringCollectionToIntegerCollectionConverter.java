package comp3111.input.converters;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import comp3111.Utils;

import java.util.Collection;
import java.util.Set;

public class StringCollectionToIntegerCollectionConverter implements Converter<Set<String>, Collection<Integer>> {
	
	@Override
	public Result<Collection<Integer>> convertToModel(Set<String> value, ValueContext context) {
		try {
			return Result.ok(Utils.stringDayNameSetToIntegerSet(value));
		} catch (Exception e) {
			return Result.error("Please enter a proper response");
		}
	}

	@Override
	public Set<String> convertToPresentation(Collection<Integer> value, ValueContext context) {
		return Utils.integerSetToStringDayNameSet(value);
	}
	
	public Class<Collection> getModelType() {
	    return Collection.class;
	  }

	  public Class<Set> getPresentationType() {
	    return Set.class;
	  }


}
