package lark.db.sql.converter;

import java.sql.Date;
import java.time.LocalDate;

/**
 * @author cuigh
 */
public class LocalDateConverter  implements Converter<LocalDate> {
    @Override
    public Object j2d(LocalDate value) {
        return Date.valueOf(value);
    }

    @Override
    public LocalDate d2j(Class<LocalDate> type, Object value) {
        return ((Date)value).toLocalDate();
    }
}
