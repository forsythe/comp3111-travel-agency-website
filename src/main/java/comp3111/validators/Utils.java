package comp3111.validators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractField;

public class Utils {

	public static void addValidator(AbstractField<?> field, Validator validator) {
		field.addValueChangeListener(event -> {
			ValidationResult result = validator.apply(event.getValue(), new ValueContext(field));

			if (result.isError()) {
				UserError error = new UserError(result.getErrorMessage());
				field.setComponentError(error);
			} else {
				field.setComponentError(null);
			}
		});
	}

	public static Set<Date> stringToDateSet(String listOfDates) {
		Set<Date> dates = new HashSet<Date>();
		String[] temp = listOfDates.replace(" ", "").split(",");

		SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yy");
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

	public static Set<Integer> stringDayNameSetToIntegerSet(Collection<String> strings) {
		Set<Integer> ints = new HashSet<Integer>();
		for (String s : strings) {
			ints.add(stringToDay(s));
		}
		return ints;
	}

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

	public static Boolean containsIgnoreCase(String fullString, String search) {
		return fullString.toLowerCase().contains(search.toLowerCase());
	}

	public static <T> Boolean collectionContainsIgnoreCase(Collection<T> arr, String search) {
		for (Object s : arr) {
			if (containsIgnoreCase(s.toString(), search)) {
				return true;
			}
		}
		return false;
	}

	public static String simpleDateFormat(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(d);
	}

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

}