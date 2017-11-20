package comp3111.input.converters;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import comp3111.Utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Converts between LocalDate objects and Date objects
 * @author Forsythe
 *
 */
public class LocalDateToUtilDateConverter implements Converter<LocalDate, Date> {

	@Override
	public Result<Date> convertToModel(LocalDate local, ValueContext context) {
		if (local == null)
			return Result.error("invalid date");
		return Result.ok(Utils.localDateToDate(local));
	}

	@Override
	public LocalDate convertToPresentation(Date date, ValueContext context) {
		if (date == null)
			return LocalDate.now();
		return date.toInstant().atZone(ZoneId.of(Utils.TIMEZONE)).toLocalDate();
	}
}
