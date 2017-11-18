package comp3111.input.editors;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.SerializablePredicate;

/**
 * A struct to hold a Provider and Predicate for filtering Vaadin grids
 * 
 * @author Forsythe
 * @param <TYPE>
 *            The class type, e.g. Person
 * @param <PROVIDEDVALUE>
 *            The data type, e.g. int
 */
public class ProviderAndPredicate<TYPE, PROVIDEDVALUE> {
	public ValueProvider<TYPE, PROVIDEDVALUE> provider;
	public SerializablePredicate<PROVIDEDVALUE> predicate;

	/**
	 * @param p
	 *            the ValueProvider object, e.g. Person::getAge
	 * @param f
	 *            the SerializablePredicate object, e.g. "age &gt; 5"
	 */
	public ProviderAndPredicate(ValueProvider<TYPE, PROVIDEDVALUE> p, SerializablePredicate<PROVIDEDVALUE> f) {
		this.provider = p;
		this.predicate = f;
	}

}