package comp3111.input.converters;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

import comp3111.Utils;

public class LocalDateTimeToUtilDateTimeConverter implements Converter<LocalDateTime, Date> {

	@Override
	public Result<Date> convertToModel(LocalDateTime local, ValueContext context) {
		if (local == null)
			return Result.error("invalid date");
		return Result.ok(Utils.localDateTimeToDate(local));
	}

	@Override
	public LocalDateTime convertToPresentation(Date date, ValueContext context) {
		if (date == null)
			return LocalDateTime.now();
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
}
