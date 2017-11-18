package comp3111.input.editors;

import comp3111.Utils;
import comp3111.data.GridCol;
import comp3111.data.model.Booking;
import comp3111.data.model.Customer;
import comp3111.data.model.NonFAQQuery;
import comp3111.data.model.Offering;
import comp3111.data.model.PromoEvent;
import comp3111.data.model.Tour;
import comp3111.data.model.TourGuide;
import comp3111.input.exceptions.ColumnNameNotFoundException;

/**
 * A factory for generating filters for vaadin grid columns
 * 
 * @author Forsythe
 *
 */
public class FilterFactory {

	/**
	 * @param colId
	 *            The id of the column
	 * @param searchVal
	 *            The value to search for
	 * @return A filter for the tour grid
	 * @throws ColumnNameNotFoundException
	 *             If the column id doesn't exist
	 */
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
		if (colId.equals(GridCol.TOUR_ID))
			return new ProviderAndPredicate<Tour, Long>(Tour::getId, t -> Utils.safeParseLongEquals(t, searchVal));
		if (colId.equals(GridCol.TOUR_TOUR_NAME))
			return new ProviderAndPredicate<Tour, String>(Tour::getTourName,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(GridCol.TOUR_DAYS))
			return new ProviderAndPredicate<Tour, Integer>(Tour::getDays, t -> Utils.safeParseIntEquals(t, searchVal));
		if (colId.equals(GridCol.TOUR_OFFERING_AVAILABILITY))
			return new ProviderAndPredicate<Tour, String>(Tour::getOfferingAvailability,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(GridCol.TOUR_DESCRIPTION))
			return new ProviderAndPredicate<Tour, String>(Tour::getDescription,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(GridCol.TOUR_WEEKDAY_PRICE))
			return new ProviderAndPredicate<Tour, Integer>(Tour::getWeekdayPrice,
					t -> Utils.safeParseIntEquals(t, searchVal));
		if (colId.equals(GridCol.TOUR_WEEKEND_PRICE))
			return new ProviderAndPredicate<Tour, Integer>(Tour::getWeekendPrice,
					t -> Utils.safeParseIntEquals(t, searchVal));
		if (colId.equals(GridCol.TOUR_CHILD_DISCOUNT))
			return new ProviderAndPredicate<Tour, Double>(Tour::getChildDiscount,
					t -> Utils.safeParseDoubleEquals(t, searchVal));
		if (colId.equals(GridCol.TOUR_TODDLER_DISCOUNT))
			return new ProviderAndPredicate<Tour, Double>(Tour::getToddlerDiscount,
					t -> Utils.safeParseDoubleEquals(t, searchVal));
		if (colId.equals(GridCol.TOUR_IS_CHILD_FRIENDLY))
			return new ProviderAndPredicate<Tour, Boolean>(Tour::isChildFriendly,
					t -> Utils.containsIgnoreCase(t.toString(), searchVal));

		throw new ColumnNameNotFoundException("[" + colId + "] isn't a valid column id for [Tour]");
	}

	/**
	 * @param colId
	 *            The id of the column
	 * @param searchVal
	 *            The value to search for
	 * @return A filter for the tour guide grid
	 * @throws ColumnNameNotFoundException
	 *             If the column id doesn't exist
	 */
	public static ProviderAndPredicate<TourGuide, ?> getFilterForTourGuide(String colId, String searchVal)
			throws ColumnNameNotFoundException {
		if (colId.equals(GridCol.TOURGUIDE_ID))
			return new ProviderAndPredicate<TourGuide, Long>(TourGuide::getId,
					t -> Utils.safeParseLongEquals(t, searchVal));
		if (colId.equals(GridCol.TOURGUIDE_NAME))
			return new ProviderAndPredicate<TourGuide, String>(TourGuide::getName,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(GridCol.TOURGUIDE_LINE_USERNAME))
			return new ProviderAndPredicate<TourGuide, String>(TourGuide::getLineUsername,
					t -> Utils.containsIgnoreCase(t, searchVal));

		throw new ColumnNameNotFoundException("[" + colId + "] isn't a valid column id for [TourGuide]");
	}

	/**
	 * @param colId
	 *            The id of the column
	 * @param searchVal
	 *            The value to search for
	 * @return A filter for the tour customer
	 * @throws ColumnNameNotFoundException
	 *             If the column id doesn't exist
	 */
	public static ProviderAndPredicate<Customer, ?> getFilterForCustomer(String colId, String searchVal)
			throws ColumnNameNotFoundException {
		if (colId.equals(GridCol.CUSTOMER_ID))
			return new ProviderAndPredicate<Customer, Long>(Customer::getId,
					t -> Utils.safeParseLongEquals(t, searchVal));
		if (colId.equals(GridCol.CUSTOMER_NAME))
			return new ProviderAndPredicate<Customer, String>(Customer::getName,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(GridCol.CUSTOMER_LINE_ID))
			return new ProviderAndPredicate<Customer, String>(Customer::getLineId,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(GridCol.CUSTOMER_HKID))
			return new ProviderAndPredicate<Customer, String>(Customer::getHkid,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(GridCol.CUSTOMER_PHONE))
			return new ProviderAndPredicate<Customer, String>(Customer::getPhone,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(GridCol.CUSTOMER_AGE))
			return new ProviderAndPredicate<Customer, Integer>(Customer::getAge,
					t -> Utils.safeParseIntEquals(t, searchVal));

		throw new ColumnNameNotFoundException("[" + colId + "] isn't a valid column id for [Customer]");
	}

	/**
	 * @param colId
	 *            The id of the column
	 * @param searchVal
	 *            The value to search for
	 * @return A filter for the booking grid
	 * @throws ColumnNameNotFoundException
	 *             If the column id doesn't exist
	 */
	public static ProviderAndPredicate<Booking, ?> getFilterForBooking(String colId, String searchVal)
			throws ColumnNameNotFoundException {
		if (colId.equals(GridCol.BOOKING_NUM_CHILDREN))
			return new ProviderAndPredicate<Booking, Integer>(Booking::getNumChildren,
					t -> Utils.safeParseIntEquals(t, searchVal));
		if (colId.equals(GridCol.BOOKING_NUM_ADULTS))
			return new ProviderAndPredicate<Booking, Integer>(Booking::getNumAdults,
					t -> Utils.safeParseIntEquals(t, searchVal));
		if (colId.equals(GridCol.BOOKING_NUM_TODDLERS))
			return new ProviderAndPredicate<Booking, Integer>(Booking::getNumToddlers,
					t -> Utils.safeParseIntEquals(t, searchVal));
		if (colId.equals(GridCol.BOOKING_CUSTOMER_NAME))
			return new ProviderAndPredicate<Booking, String>(Booking::getCustomerName,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(GridCol.BOOKING_CUSTOMER_HKID))
			return new ProviderAndPredicate<Booking, String>(Booking::getCustomerHkid,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(GridCol.BOOKING_OFFERING_ID))
			return new ProviderAndPredicate<Booking, Long>(Booking::getOfferingId,
					t -> Utils.safeParseLongEquals(t, searchVal));
		if (colId.equals(GridCol.BOOKING_TOUR_ID))
			return new ProviderAndPredicate<Booking, Long>(Booking::getTourId,
					t -> Utils.safeParseLongEquals(t, searchVal));
		if (colId.equals(GridCol.BOOKING_TOUR_NAME))
			return new ProviderAndPredicate<Booking, String>(Booking::getTourName,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(GridCol.BOOKING_PEOPLE))
			return new ProviderAndPredicate<Booking, String>(Booking::getPeople,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(GridCol.BOOKING_AMOUNT_PAID))
			return new ProviderAndPredicate<Booking, Double>(Booking::getAmountPaid,
					t -> Utils.safeParseDoubleEquals(t, searchVal));
		if (colId.equals(GridCol.BOOKING_TOTAL_COST))
			return new ProviderAndPredicate<Booking, Double>(Booking::getTotalCost,
					t -> Utils.safeParseDoubleEquals(t, searchVal));
		if (colId.equals(GridCol.BOOKING_SPECIAL_REQUEST))
			return new ProviderAndPredicate<Booking, String>(Booking::getSpecialRequests,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(GridCol.BOOKING_PAYMENT_STATUS))
			return new ProviderAndPredicate<Booking, String>(Booking::getPaymentStatus,
					t -> Utils.containsIgnoreCase(t, searchVal));

		if (colId.equals(GridCol.BOOKING_PROMO_DISCOUNT_MULTIPLIER_MASKED)) {
			if (Utils.containsIgnoreCase("none", searchVal)) {
				return new ProviderAndPredicate<>(Booking::getPromoDiscountMultiplier,
						t -> Utils.compareDoubleAsIs(t, 1.0));
			} else {
				return new ProviderAndPredicate<>(Booking::getPromoDiscountMultiplier,
						t -> Utils.safeParseDoubleEquals(t, searchVal));
			}
		}

		throw new ColumnNameNotFoundException("[" + colId + "] isn't a valid column id for [Booking]");
	}

	/**
	 * @param colId
	 *            The id of the column
	 * @param searchVal
	 *            The value to search for
	 * @return A filter for the NonFAQQuery grid
	 * @throws ColumnNameNotFoundException
	 *             If the column id doesn't exist
	 */
	public static ProviderAndPredicate<NonFAQQuery, ?> getFilterForNonFAQQuery(String colId, String searchVal)
			throws ColumnNameNotFoundException {
		if (colId.equals(GridCol.NONFAQQUERY_ID))
			return new ProviderAndPredicate<NonFAQQuery, Long>(NonFAQQuery::getId,
					t -> Utils.safeParseLongEquals(t, searchVal));
		if (colId.equals(GridCol.NONFAQQUERY_ANSWER))
			return new ProviderAndPredicate<NonFAQQuery, String>(NonFAQQuery::getAnswer,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(GridCol.NONFAQQUERY_QUERY))
			return new ProviderAndPredicate<NonFAQQuery, String>(NonFAQQuery::getQuery,
					t -> Utils.containsIgnoreCase(t, searchVal));
		// TODO finish this part of the factory
		if (colId.equals(GridCol.NONFAQQUERY_CUSTOMER))
			return new ProviderAndPredicate<NonFAQQuery, String>(NonFAQQuery::getCustomerName,
					t -> Utils.containsIgnoreCase(t, searchVal));

		throw new ColumnNameNotFoundException("[" + colId + "] isn't a valid column id for [NonFAQQuery]");
	}

	/**
	 * @param colId
	 *            The id of the column
	 * @param searchVal
	 *            The value to search for
	 * @return A filter for the offering grid
	 * @throws ColumnNameNotFoundException
	 *             If the column id doesn't exist
	 */
	public static ProviderAndPredicate<Offering, ?> getFilterForOffering(String colId, String searchVal)
			throws ColumnNameNotFoundException {
		if (colId.equals(GridCol.OFFERING_ID))
			return new ProviderAndPredicate<Offering, Long>(Offering::getId,
					t -> Utils.safeParseLongEquals(t, searchVal));
		if (colId.equals(GridCol.OFFERING_HOTEL_NAME))
			return new ProviderAndPredicate<Offering, String>(Offering::getHotelName,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(GridCol.OFFERING_START_DATE))
			return new ProviderAndPredicate<Offering, String>(Offering::getStartDateString,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(GridCol.OFFERING_TOUR_GUIDE_NAME))
			return new ProviderAndPredicate<Offering, String>(Offering::getTourGuideName,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(GridCol.OFFERING_TOUR_GUIDE_LINE_ID))
			return new ProviderAndPredicate<Offering, String>(Offering::getTourGuideLineId,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(GridCol.OFFERING_TOUR_NAME))
			return new ProviderAndPredicate<Offering, String>(Offering::getTourName,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(GridCol.OFFERING_MIN_CAPACITY))
			return new ProviderAndPredicate<Offering, Integer>(Offering::getMinCustomers,
					t -> Utils.safeParseIntEquals(t, searchVal));
		if (colId.equals(GridCol.OFFERING_MAX_CAPACITY))
			return new ProviderAndPredicate<Offering, Integer>(Offering::getMaxCustomers,
					t -> Utils.safeParseIntEquals(t, searchVal));
		if (colId.equals(GridCol.OFFERING_STATUS))
			return new ProviderAndPredicate<Offering, String>(Offering::getStatus,
					t -> Utils.containsIgnoreCase(t, searchVal));

		throw new ColumnNameNotFoundException("[" + colId + "] isn't a valid column id for [Offering]");
	}

	/**
	 * @param colId
	 *            The id of the column
	 * @param searchVal
	 *            The value to search for
	 * @return A filter for the PromoEvent grid
	 * @throws ColumnNameNotFoundException
	 *             If the column id doesn't exist
	 */
	public static ProviderAndPredicate<PromoEvent, ?> getFilterForPromoEvent(String colId, String searchVal)
			throws ColumnNameNotFoundException {
		if (colId.equals(GridCol.PROMOEVENT_ID))
			return new ProviderAndPredicate<PromoEvent, Long>(PromoEvent::getId,
					t -> Utils.safeParseLongEquals(t, searchVal));
		if (colId.equals(GridCol.PROMOEVENT_PROMO_CODE))
			return new ProviderAndPredicate<PromoEvent, String>(PromoEvent::getPromoCode,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(GridCol.PROMOEVENT_PROMO_CODE_USES_LEFT))
			return new ProviderAndPredicate<PromoEvent, Integer>(PromoEvent::getPromoCodeUsesLeft,
					t -> Utils.safeParseIntEquals(t, searchVal));
		if (colId.equals(GridCol.PROMOEVENT_DISCOUNT))
			return new ProviderAndPredicate<PromoEvent, Double>(PromoEvent::getDiscount,
					t -> Utils.safeParseDoubleEquals(t, searchVal));
		if (colId.equals(GridCol.PROMOEVENT_CUSTOM_MESSAGE))
			return new ProviderAndPredicate<PromoEvent, String>(PromoEvent::getCustomMessage,
					t -> Utils.containsIgnoreCase(t, searchVal));
		if (colId.equals(GridCol.PROMOEVENT_MAX_RESERVATIONS_PER_CUSTOMER))
			return new ProviderAndPredicate<PromoEvent, Integer>(PromoEvent::getMaxReservationsPerCustomer,
					t -> Utils.safeParseIntEquals(t, searchVal));
		if (colId.equals(GridCol.PROMOEVENT_OFFERING_ID))
			return new ProviderAndPredicate<PromoEvent, Long>(PromoEvent::getOfferingId,
					t -> Utils.safeParseLongEquals(t, searchVal));
		if (colId.equals(GridCol.PROMOEVENT_TRIGGER_DATE_STRING))
			return new ProviderAndPredicate<PromoEvent, String>(PromoEvent::getTriggerDateString,
					t -> Utils.containsIgnoreCase(t, searchVal));

		throw new ColumnNameNotFoundException("[" + colId + "] isn't a valid column id for [Offering]");
	}
}
