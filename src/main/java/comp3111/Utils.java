package comp3111;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DateTimeField;

/**
 * A class holding common utility functions
 * 
 * @author Forsythe
 *
 */
public class Utils {

	public static final String DATE_LOCALE = "dd/MM/yyyy";
	public static final String DATE_TIME_LOCALE = DATE_LOCALE + " HH:mm:ss Z";
	public static final String TIMEZONE = "Hongkong";

	/**
	 * Returns the error to show when a required field is left empty
	 * 
	 * @return The error to show when a required field is left empty
	 */
	public static String generateRequiredError() {
		return "cannot be empty";
	}

	/**
	 * Returns the error to show when no tour guides are available
	 * 
	 * @return The error to show when no tour guides are available
	 */
	public static String generateNoTourGuideAvailableError() {
		return "no tour guides are free for this date";
	}

	/**
	 * Converts a string to a set of dates
	 * 
	 * @param listOfDates
	 *            A string containing comma separated values of dates
	 * @return A collection of Date objects
	 */
	public static Collection<Date> stringToDateSet(String listOfDates) {
		Set<Date> dates = new HashSet<Date>();
		if (listOfDates == null || listOfDates.isEmpty())
			return dates;
		String[] temp = listOfDates.replace(" ", "").split(",");

		SimpleDateFormat parser = new SimpleDateFormat(DATE_LOCALE);
		parser.setLenient(false);

		for (String s : temp) {
			try {
				dates.add(parser.parse(s));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return dates;
	}

	/**
	 * Converts a collection of integers to a collection of strings representing
	 * days of the week
	 * 
	 * @param integers
	 *            The collection of integers
	 * @return A set of strings representing the weekday names
	 */
	public static Set<String> integerSetToStringDayNameSet(Collection<Integer> integers) {
		Set<String> strings = new HashSet<String>();
		for (Integer i : integers) {
			strings.add(dayToString(i));
		}
		return strings;
	}

	/**
	 * Converts a collection of strings representing days of the week to a
	 * collection of integers
	 * 
	 * @param strings
	 *            A collection of strings representing the weekday names
	 * @return The collection of integers representing the days of the week
	 */
	public static Collection<Integer> stringDayNameSetToIntegerSet(Collection<String> strings) {
		Set<Integer> ints = new HashSet<Integer>();
		for (String s : strings) {
			ints.add(stringToDay(s));
		}
		return ints;
	}

	/**
	 * Converts an integer representing a day of the week to the string
	 * representation
	 * 
	 * @param day
	 *            The integer representing the day of the week
	 * @return The string representation of the day of the week
	 */
	public static String dayToString(int day) {
		switch (day) {
		case Calendar.MONDAY:
			return "Mon";
		case Calendar.TUESDAY:
			return "Tue";
		case Calendar.WEDNESDAY:
			return "Wed";
		case Calendar.THURSDAY:
			return "Thu";
		case Calendar.FRIDAY:
			return "Fri";
		case Calendar.SATURDAY:
			return "Sat";
		case Calendar.SUNDAY:
			return "Sun";
		default:
			return "invalid day";
		}
	}

	/**
	 * Converts a string representation of a day of the week to an integer
	 * 
	 * @param s
	 *            The day of the week, (Mon, Tue, Wed, Thu, Fri, Sat, Sun)
	 * @return The integer representation
	 */
	public static Integer stringToDay(String s) {
		if (s.equals("Mon"))
			return Calendar.MONDAY;
		else if (s.equals("Tue"))
			return Calendar.TUESDAY;
		else if (s.equals("Wed"))
			return Calendar.WEDNESDAY;
		else if (s.equals("Thu"))
			return Calendar.THURSDAY;
		else if (s.equals("Fri"))
			return Calendar.FRIDAY;
		else if (s.equals("Sat"))
			return Calendar.SATURDAY;
		else if (s.equals("Sun"))
			return Calendar.SUNDAY;
		else
			return -1;
	}

	/**
	 * Returns a collection of all days of the week
	 * 
	 * @return A collection of strings containing all the days of the week
	 */
	public static Collection<String> getDaysOfWeek() {
		return Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
	}

	/**
	 * Checks if a string contains a substring, ignoring case
	 * 
	 * @param fullString
	 *            The full string
	 * @param search
	 *            The substring to search for
	 * @return Whether or not the substring was found
	 */
	public static Boolean containsIgnoreCase(String fullString, String search) {
		return fullString.toLowerCase().contains(search.toLowerCase());
	}

	/**
	 * For each element of a collection, checks if the string representation of each
	 * element contains a specified substring
	 * 
	 * @param arr
	 *            The collection of elements
	 * @param search
	 *            The substring to search for per each element's string
	 *            representation
	 * @param <T>
	 *            Any object with an overridden toString()
	 * @return whether any of the elements' string representations contained the
	 *         provided substring
	 */
	public static <T> Boolean collectionContainsIgnoreCase(Collection<T> arr, String search) {
		for (Object s : arr) {
			if (containsIgnoreCase(s.toString(), search)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Converts a Date object to a nicely formatted string
	 * 
	 * @param d
	 *            The Date object
	 * @return A nicely formatted string following {@link #DATE_LOCALE}
	 */
	public static String simpleDateFormat(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_LOCALE);
		return sdf.format(d);
	}

	/**
	 * Parses a string into a Date object
	 * 
	 * @param s
	 *            The string
	 * @return A Date object
	 * @throws ParseException
	 *             If the parse was unsuccessful
	 */
	public static Date parseSimpleDateFormat(String s) throws ParseException {
		SimpleDateFormat parser = new SimpleDateFormat(DATE_LOCALE);
		parser.setLenient(false);
		return parser.parse(s);
	}

	/**
	 * Safely parses an integer, and check if it equals a value
	 * 
	 * @param val
	 *            The expected value
	 * @param s
	 *            The string to parse
	 * @return Whether the parsed value matches the expected value
	 */
	public static Boolean safeParseIntEquals(int val, String s) {
		try {
			if (Integer.parseInt(s) == val) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * Safely parses a boolean, and check if it equals a value
	 * 
	 * @param val
	 *            The expected value
	 * @param s
	 *            The string to parse
	 * @return Whether the parsed value matches the expected value
	 */
	public static Boolean safeParseBoolEquals(boolean val, String s) {
		try {
			if (Boolean.parseBoolean(s) == val) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * Safely parses a long, and check if it equals a value
	 * 
	 * @param val
	 *            The expected value
	 * @param s
	 *            The string to parse
	 * @return Whether the parsed value matches the expected value
	 */
	public static Boolean safeParseLongEquals(Long val, String s) {
		try {
			if (Long.parseLong(s) == val) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * Safely parses a double, and check if it equals a value
	 * 
	 * @param val
	 *            The expected value
	 * @param s
	 *            The string to parse
	 * @return Whether the parsed value matches the expected value
	 */
	public static Boolean safeParseDoubleEquals(Double val, String s) {
		try {
			if (Double.parseDouble(s) == val) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * Converts a collection of dates into a single formatted string
	 * 
	 * @param dates
	 *            A collection of dates
	 * @return A nicely formatted string of the dates, with commas in between
	 */
	public static String dateCollectionToString(Collection<Date> dates) {
		ArrayList<String> dateList = new ArrayList<>();
		for (Date day : dates) {
			dateList.add(Utils.simpleDateFormat(day));
		}
		return String.join(", ", dateList);
	}

	/**
	 * Converts a collection of integers (representing days of the week) to a single
	 * formatted string
	 * 
	 * @param integerCollection
	 *            A collection of integers representing days of the week
	 * @return A nicely formatted string of the days, with commas in between; (e.g.
	 *         Mon, Tue, Wed)
	 */
	public static String integerCollectionToString(Collection<Integer> integerCollection) {
		ArrayList<String> integerList = new ArrayList<>();
		for (int integer : integerCollection) {
			integerList.add(Utils.dayToString(integer));
		}

		return String.join(", ", integerList);
	}

	/**
	 * Adds days to a date, to get a new Date object
	 * 
	 * @param d
	 *            The original date
	 * @param days
	 *            How many days to add
	 * @return The new Date object
	 */
	public static Date addDate(Date d, int days) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	/**
	 * Converts LocalDate objects to Date objects
	 * 
	 * @param local
	 *            The LocalDate object
	 * @return The corresponding Date object
	 */
	public static Date localDateToDate(LocalDate local) {
		return java.sql.Date.valueOf(local);
	}

	/**
	 * Converts LocalDateTime objects to Date objects
	 * 
	 * @param local
	 *            The LocalDateTime object
	 * @return The corresponding Date object
	 */
	public static Date localDateTimeToDate(LocalDateTime local) {
		return Date.from(local.atZone(ZoneId.of(TIMEZONE)).toInstant());
	}

	/**
	 * Adds days to a LocalDate object
	 * 
	 * @param value
	 *            The LocalDate object
	 * @param days
	 *            The number of days to add
	 * @return The corresponding Date object
	 */
	public static Date addDate(LocalDate value, int days) {
		return addDate(localDateToDate(value), days);
	}

	/**
	 * Converts an iterable to a collection
	 * 
	 * @param iterable
	 *            The iterable
	 * @param <T>
	 *            Any type
	 * @return A collection containing all the elements in the Iterable
	 */
	public static <T> Collection<T> iterableToCollection(Iterable<T> iterable) {
		Collection<T> c = new ArrayList<T>();
		for (T o : iterable) {
			c.add(o);
		}
		return c;
	}

	/**
	 * Returns a Vaadin DateField which follows our {@link #DATE_LOCALE}
	 * 
	 * @param caption
	 *            The caption of the input box
	 * @return The DateField object
	 */
	public static DateField getDateFieldWithOurLocale(String caption) {
		DateField d = new DateField(caption);
		//d.setDateFormat(DATE_LOCALE);
		return d;
	}

	/**
	 * Returns a Vaadin DateTimeField which follows our {@link #DATE_TIME_LOCALE}
	 * 
	 * @param caption
	 *            The caption of the input box
	 * @return The DateTimeField object
	 */
	public static DateTimeField getDateTimeFieldWithOurLocale(String caption) {
		DateTimeField d = new DateTimeField(caption);
		//d.setDateFormat(DATE_TIME_LOCALE);
		d.setResolution(DateTimeResolution.MINUTE);
		return d;
	}

}