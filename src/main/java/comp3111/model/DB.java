package comp3111.model;

public class DB {
	/*
	 * for easy referencing of column IDs in vaadin they should match the getters
	 * and setters' name. Note: these names must match the getters of the class
	 * 
	 * e.g. <Class>'s getTourName() ---> <CLASS>_TOUR_NAME = "tourName"
	 */
	public static final String HIBERNATE_NEW_COL = "new";

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
	public static final String TOUR_OFFERINGS = "offerings";
	/* hidden from grid */
	public static final String TOUR_ALLOWED_DAYS_OF_WEEK = "allowedDaysOfWeek";
	public static final String TOUR_ALLOWED_DATES = "allowedDates";

}
