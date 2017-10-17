package comp3111.presenter;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.SerializablePredicate;

/*
 * a struct to hold a provider (e.g. Person::getAge) and predicate (boolean
 * expression), (e.g. "s>5", when s is an int). used for grid filtering
 * 
 * T: the provider class (e.g. Tour), as well as the input to the predicate
 * V: the provided value (e.g. int)
 */
public class ProviderAndPredicate<TYPE, PROVIDEDVALUE> {
	public ValueProvider<TYPE, PROVIDEDVALUE> provider;
	public SerializablePredicate<PROVIDEDVALUE> predicate;

	public ProviderAndPredicate(ValueProvider<TYPE, PROVIDEDVALUE> p, SerializablePredicate<PROVIDEDVALUE> f) {
		this.provider = p;
		this.predicate = f;
	}

}