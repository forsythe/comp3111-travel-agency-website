/**
 * 
 */
package unit;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import comp3111.data.GridCol;
import comp3111.data.model.Booking;
import comp3111.data.model.Customer;
import comp3111.data.model.NonFAQQuery;
import comp3111.data.model.Offering;
import comp3111.data.model.PromoEvent;
import comp3111.data.model.Tour;
import comp3111.data.model.TourGuide;
import comp3111.input.editors.FilterFactory;
import comp3111.input.exceptions.ColumnNameNotFoundException;

/**
 * @author Forsythe
 *
 */
public class FilterFactoryTest {

	@Test
	public void testSuccessMakeFiltersForTourTable() throws ColumnNameNotFoundException {
		String[] colIds = new String[] { GridCol.TOUR_ID, GridCol.TOUR_TOUR_NAME, GridCol.TOUR_DAYS,
				GridCol.TOUR_OFFERING_AVAILABILITY, GridCol.TOUR_DESCRIPTION, GridCol.TOUR_WEEKDAY_PRICE,
				GridCol.TOUR_WEEKEND_PRICE, GridCol.TOUR_CHILD_DISCOUNT, GridCol.TOUR_TODDLER_DISCOUNT,
				GridCol.TOUR_IS_CHILD_FRIENDLY };

		for (String colId : colIds) {
			assertNotNull(FilterFactory.getFilters(Tour.class, colId, "search"));
		}
	}

	@Test(expected = ColumnNameNotFoundException.class)
	public void testFailMakeFilterForTourTableInvalidColId() throws ColumnNameNotFoundException {
		FilterFactory.getFilters(Tour.class, "i want to column", "what");
	}

	@Test
	public void testSuccessMakeFiltersForTourGuideTable() throws ColumnNameNotFoundException {
		String[] colIds = new String[] { GridCol.TOURGUIDE_ID, GridCol.TOURGUIDE_NAME,
				GridCol.TOURGUIDE_LINE_USERNAME };

		for (String colId : colIds) {
			assertNotNull(FilterFactory.getFilters(TourGuide.class, colId, "search"));
		}
	}

	@Test(expected = ColumnNameNotFoundException.class)
	public void testFailMakeFilterForTourGuideTableInvalidColId() throws ColumnNameNotFoundException {
		FilterFactory.getFilters(TourGuide.class, "i want to column", "what");
	}

	@Test
	public void testSuccessMakeFiltersForCustomerTable() throws ColumnNameNotFoundException {
		String[] colIds = new String[] { GridCol.CUSTOMER_ID, GridCol.CUSTOMER_NAME, GridCol.CUSTOMER_LINE_ID,
				GridCol.CUSTOMER_HKID, GridCol.CUSTOMER_PHONE, GridCol.CUSTOMER_AGE };

		for (String colId : colIds) {
			assertNotNull(FilterFactory.getFilters(Customer.class, colId, "search"));
		}
	}

	@Test(expected = ColumnNameNotFoundException.class)
	public void testFailMakeFilterForCustomerTableInvalidColId() throws ColumnNameNotFoundException {
		FilterFactory.getFilters(Customer.class, "i want to column", "what");
	}

	@Test
	public void testSuccessMakeFiltersForBookingTable() throws ColumnNameNotFoundException {
		String[] colIds = new String[] { GridCol.BOOKING_NUM_CHILDREN, GridCol.BOOKING_NUM_ADULTS,
				GridCol.BOOKING_NUM_TODDLERS, GridCol.BOOKING_OFFERING, GridCol.BOOKING_CUSTOMER_HKID,
				GridCol.BOOKING_CUSTOMER_NAME, GridCol.BOOKING_TOUR_ID, GridCol.BOOKING_TOUR_NAME,
				GridCol.BOOKING_AMOUNT_PAID, GridCol.BOOKING_TOTAL_COST, GridCol.BOOKING_SPECIAL_REQUEST,
				GridCol.BOOKING_PAYMENT_STATUS, GridCol.BOOKING_PROMO_DISCOUNT_MULTIPLIER_MASKED };

		for (String colId : colIds) {
			assertNotNull(FilterFactory.getFilters(Booking.class, colId, "search"));
		}
	}

	@Test(expected = ColumnNameNotFoundException.class)
	public void testFailMakeFilterForBookingTableInvalidColId() throws ColumnNameNotFoundException {
		FilterFactory.getFilters(Booking.class, "i want to column", "what");
	}

	@Test
	public void testSuccessMakeFiltersForNonFAQQueryTable() throws ColumnNameNotFoundException {
		String[] colIds = new String[] { GridCol.NONFAQQUERY_ID, GridCol.NONFAQQUERY_ANSWER, GridCol.NONFAQQUERY_QUERY,
				GridCol.NONFAQQUERY_CUSTOMER };

		for (String colId : colIds) {
			assertNotNull(FilterFactory.getFilters(NonFAQQuery.class, colId, "search"));
		}
	}

	@Test(expected = ColumnNameNotFoundException.class)
	public void testFailMakeFilterForNonFAQQueryTableInvalidColId() throws ColumnNameNotFoundException {
		FilterFactory.getFilters(NonFAQQuery.class, "i want to column", "what");
	}

	@Test
	public void testSuccessMakeFiltersForOfferingTable() throws ColumnNameNotFoundException {
		String[] colIds = new String[] { GridCol.OFFERING_ID, GridCol.OFFERING_HOTEL_NAME, GridCol.OFFERING_START_DATE,
				GridCol.OFFERING_TOUR_GUIDE_NAME, GridCol.OFFERING_TOUR_GUIDE_LINE_ID, GridCol.OFFERING_TOUR_NAME,
				GridCol.OFFERING_MIN_CAPACITY, GridCol.OFFERING_MAX_CAPACITY, GridCol.OFFERING_STATUS };

		for (String colId : colIds) {
			assertNotNull(FilterFactory.getFilters(Offering.class, colId, "search"));
		}
	}

	@Test(expected = ColumnNameNotFoundException.class)
	public void testFailMakeFilterForOfferingTableInvalidColId() throws ColumnNameNotFoundException {
		FilterFactory.getFilters(Offering.class, "i want to column", "what");
	}

	@Test
	public void testSuccessMakeFiltersForPromoEventTable() throws ColumnNameNotFoundException {
		String[] colIds = new String[] { GridCol.PROMOEVENT_ID, GridCol.PROMOEVENT_PROMO_CODE,
				GridCol.PROMOEVENT_PROMO_CODE_USES_LEFT, GridCol.PROMOEVENT_DISCOUNT, GridCol.PROMOEVENT_CUSTOM_MESSAGE,
				GridCol.PROMOEVENT_MAX_RESERVATIONS_PER_CUSTOMER, GridCol.PROMOEVENT_OFFERING,
				GridCol.PROMOEVENT_TRIGGER_DATE, GridCol.PROMOEVENT_IS_TRIGGERED };

		for (String colId : colIds) {
			assertNotNull(FilterFactory.getFilters(PromoEvent.class, colId, "search"));
		}
	}

	@Test(expected = ColumnNameNotFoundException.class)
	public void testFailMakeFilterForPromoEventTableInvalidColId() throws ColumnNameNotFoundException {
		FilterFactory.getFilters(PromoEvent.class, "i want to column", "what");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFailNoFiltersForEntity() throws ColumnNameNotFoundException {
		FilterFactory.getFilters(Object.class, "i want to column", "what");
	}

}
