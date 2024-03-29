package comp3111.data;

/**
 * Holds the grid column IDs of our Vaadin grids
 * 
 * @author Forsythe
 *
 */
public class GridCol {
	/*
	 * Each constant represents the column ID of a grid in vaadin. They MUST match
	 * the getter/setter name.
	 * 
	 * e.g. Tour's getChildDiscount() becomes TOUR_CHILD_DISCOUNT = "childDiscount"
	 * 
	 * 
	 * NB: for booleans, the convention for variable "situation" is different.
	 * getters look like "isSituation" and setters like "setSituation". Therefore
	 * for the grid ID, it should just be "situation"
	 */

	// Tour
	/* shown in grid */
	public static final String TOUR_ID = "id";
	public static final String TOUR_TOUR_NAME = "tourName";
	public static final String TOUR_DAYS = "days";
	public static final String TOUR_DESCRIPTION = "description";
	public static final String TOUR_CHILD_DISCOUNT = "childDiscount";
	public static final String TOUR_TODDLER_DISCOUNT = "toddlerDiscount";
	public static final String TOUR_WEEKDAY_PRICE = "weekdayPrice";
	public static final String TOUR_WEEKEND_PRICE = "weekendPrice";
	public static final String TOUR_OFFERING_AVAILABILITY = "offeringAvailability";
	public static final String TOUR_IS_CHILD_FRIENDLY = "childFriendly";
	/* hidden from grid */
	public static final String TOUR_ALLOWED_DAYS_OF_WEEK = "allowedDaysOfWeek";
	public static final String TOUR_ALLOWED_DATES = "allowedDates";

	// Offering
	/* shown in grid */
	public static final String OFFERING_ID = "id";
	public static final String OFFERING_HOTEL_NAME = "hotelName";
	public static final String OFFERING_START_DATE = "startDateString";
	public static final String OFFERING_TOUR_GUIDE_NAME = "tourGuideName";
	public static final String OFFERING_TOUR_GUIDE_LINE_ID = "tourGuideLineId";
	public static final String OFFERING_TOUR_NAME = "tourName";
	public static final String OFFERING_MIN_CAPACITY = "minCustomers";
	public static final String OFFERING_MAX_CAPACITY = "maxCustomers";
	public static final String OFFERING_STATUS = "status";
	/* hidden from grid */
	public static final String OFFERING_TOUR = "tour";
	public static final String OFFERING_TOUR_GUIDE = "tourGuide";
	public static final String OFFERING_DATE = "startDate";
	public static final String OFFERING_LAST_EDITABLE_DATE = "lastEditableDate";
	

	// Customer
	/* shown in grid */
	public static final String CUSTOMER_ID = "id";
	public static final String CUSTOMER_NAME = "name";
	public static final String CUSTOMER_LINE_ID = "lineId";
	public static final String CUSTOMER_HKID = "hkid";
	public static final String CUSTOMER_PHONE = "phone";
	public static final String CUSTOMER_AGE = "age";

	// Booking
	/* shown in grid */
	public static final String BOOKING_NUM_CHILDREN = "numChildren";
	public static final String BOOKING_NUM_ADULTS = "numAdults";
	public static final String BOOKING_NUM_TODDLERS = "numToddlers";
	public static final String BOOKING_OFFERING = "offering";
	public static final String BOOKING_CUSTOMER_HKID = "customerHkid";
	public static final String BOOKING_CUSTOMER_NAME = "customerName";
	public static final String BOOKING_TOUR_ID = "tourId";
	public static final String BOOKING_TOUR_NAME = "tourName";
	public static final String BOOKING_AMOUNT_PAID = "amountPaid";
	public static final String BOOKING_TOTAL_COST = "totalCost";
	public static final String BOOKING_SPECIAL_REQUEST = "specialRequests";
	public static final String BOOKING_PAYMENT_STATUS = "paymentStatus";
	public static final String BOOKING_PROMO_DISCOUNT_MULTIPLIER_MASKED = "paymentStatusMasked";
	/* hidden */
	public static final String BOOKING_ID = "id";
	public static final String BOOKING_CUSTOMER = "customer";
	public static final String BOOKING_PROMO_DISCOUNT_MULTIPLIER = "promoDiscountMultiplier";
	public static final String BOOKING_TOTAL_NUMBER_OF_PEOPLE = "totalNumberOfPeople";
	public static final String BOOKING_PROMO_CODE_USED = "promoCodeUsed";

	// TourGuide
	/* shown in grid */
	public static final String TOURGUIDE_ID = "id";
	public static final String TOURGUIDE_NAME = "name";
	public static final String TOURGUIDE_LINE_USERNAME = "lineUsername";

	// NonFAQQuery
	/* show in grd */
	public static final String NONFAQQUERY_QUERY = "query";
	public static final String NONFAQQUERY_ANSWER = "answer";
	public static final String NONFAQQUERY_CUSTOMER = "customer";
	public static final String NONFAQQUERY_ID = "id";
	/* hidden in grid */
	public static final String NONFAQQUERY_CUSTOMER_NAME = "customerName";

	// PromotionEvent
	/* show in grid */
	public static final String PROMOEVENT_ID = "id";
	public static final String PROMOEVENT_PROMO_CODE = "promoCode";
	public static final String PROMOEVENT_PROMO_CODE_USES_LEFT = "promoCodeUsesLeft";
	public static final String PROMOEVENT_DISCOUNT = "discount";
	public static final String PROMOEVENT_CUSTOM_MESSAGE = "customMessage";
	public static final String PROMOEVENT_MAX_RESERVATIONS_PER_CUSTOMER = "maxReservationsPerCustomer";
	public static final String PROMOEVENT_IS_TRIGGERED = "triggered";
	public static final String PROMOEVENT_TRIGGER_DATE = "triggerDate";
	public static final String PROMOEVENT_OFFERING = "offering";
}
