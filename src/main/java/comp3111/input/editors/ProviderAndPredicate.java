package comp3111.input.editors;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.SerializablePredicate;

/*
 * a struct to hold a provider (e.g. Person::getAge) and predicate (boolean
 * expression), (e.g. "age>5", when age is an int). used for grid filtering
 * 
 * TYPE: e.g. Person
 * PROVIDEDVALUE: e.g. int
 */
public class ProviderAndPredicate<TYPE, PROVIDEDVALUE> {
	public ValueProvider<TYPE, PROVIDEDVALUE> provider;
	public SerializablePredicate<PROVIDEDVALUE> predicate;

	public ProviderAndPredicate(ValueProvider<TYPE, PROVIDEDVALUE> p, SerializablePredicate<PROVIDEDVALUE> f) {
		this.provider = p;
		this.predicate = f;
	}

}