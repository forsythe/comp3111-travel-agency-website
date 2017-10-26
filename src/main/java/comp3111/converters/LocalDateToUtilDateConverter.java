package comp3111.converters;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class LocalDateToUtilDateConverter implements Converter<LocalDate, Date> {

    @Override
    public Result<Date> convertToModel(LocalDate local, ValueContext context) {
        return Result.ok(java.sql.Date.valueOf(local));
    }

    @Override
    public LocalDate convertToPresentation(Date date, ValueContext context) {
        if (date == null) return LocalDate.now();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
