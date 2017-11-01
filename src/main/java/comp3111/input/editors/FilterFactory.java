package comp3111.input.editors;

import comp3111.Utils;
import comp3111.data.DB;
import comp3111.data.model.Tour;
import comp3111.input.exceptions.ColumnNameNotFoundException;

/*
 * a class for getting filters, based on a vaadin grid column
 */
public class FilterFactory {

	public static ProviderAndPredicate<Tour, ?> getFilterForTour(String colId, String searchVal)
			throws ColumnNameNotFoundException {
		/*
		 * we need "safe parsing", because try catch won't work here. the predicate
		 * (boolean expression) isn't evaluated at this part, but elsewhere in the JVM.
		 *
		 * unfortunately, we cannot store these into a hashmap and look them up. this is
		 * because the predicate must be generated at runtime, based on the varying
		 * search values.
		 */
		if (colId.equals(DB.TOUR_ID))
			return new ProviderAndPredicate<Tour, Long>(Tour::getId, t -> Utils.safeParseLongEquals(t, searchVal));
		if (colId.equals(DB.TOUR_TOUR_NAME))
			return new ProviderAndPredicate<Tour, String>(Tour::getTourName,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(DB.TOUR_DAYS))
			return new ProviderAndPredicate<Tour, Integer>(Tour::getDays, t -> Utils.safeParseIntEquals(t, searchVal));
		if (colId.equals(DB.TOUR_OFFERING_AVAILABILITY))
			return new ProviderAndPredicate<Tour, String>(Tour::getOfferingAvailability,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(DB.TOUR_DESCRIPTION))
			return new ProviderAndPredicate<Tour, String>(Tour::getDescription,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(DB.TOUR_WEEKDAY_PRICE))
			return new ProviderAndPredicate<Tour, Integer>(Tour::getWeekdayPrice,
					t -> Utils.safeParseIntEquals(t, searchVal));
		if (colId.equals(DB.TOUR_WEEKEND_PRICE))
			return new ProviderAndPredicate<Tour, Integer>(Tour::getWeekendPrice,
					t -> Utils.safeParseIntEquals(t, searchVal));
		if (colId.equals(DB.TOUR_CHILD_DISCOUNT))
			return new ProviderAndPredicate<Tour, Double>(Tour::getChildDiscount,
					t -> Utils.safeParseDoubleEquals(t, searchVal));
		if (colId.equals(DB.TOUR_TODDLER_DISCOUNT))
			return new ProviderAndPredicate<Tour, Double>(Tour::getToddlerDiscount,
					t -> Utils.safeParseDoubleEquals(t, searchVal));

		throw new ColumnNameNotFoundException("[" + colId + "] isn't a valid column id for [Tour]");
	}
}
