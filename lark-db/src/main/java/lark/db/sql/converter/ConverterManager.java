package lark.db.sql.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author cuigh
 */
@SuppressWarnings("unchecked")
public final class ConverterManager {
    private static final Converter ENUM = new EnumConverter();
    private static final Converter DATE = new DateConverter();
    private static final Converter LOCAL_DATE = new LocalDateConverter();
    private static final Converter LOCAL_DATE_TIME = new LocalDateTimeConverter();
    private static final Converter ZONED_DATE_TIME = new ZonedDateTimeConverter();

    public static Object j2d(Object value) {
        if (value == null) {
            return null;
        }

        Class<?> clazz = value.getClass();
        if (clazz == Date.class) {
            return DATE.j2d(value);
        } else if (clazz == LocalDateTime.class) {
            return LOCAL_DATE_TIME.j2d(value);
        } else if (clazz == LocalDate.class) {
            return LOCAL_DATE.j2d(value);
        } else if (clazz == ZonedDateTime.class) {
            return ZONED_DATE_TIME.j2d(value);
        } else if (clazz.isEnum()) {
            return ENUM.j2d(value);
        }
        return value;
    }

    public static Object d2j(Class<?> clazz, Object value) {
        if (clazz == Date.class) {
            return DATE.d2j(clazz, value);
        } else if (clazz == LocalDateTime.class) {
            return LOCAL_DATE_TIME.d2j(clazz, value);
        } else if (clazz == LocalDate.class) {
            return LOCAL_DATE.d2j(clazz, value);
        } else if (clazz == ZonedDateTime.class) {
            return ZONED_DATE_TIME.d2j(clazz, value);
        } else if (clazz.isEnum()) {
            return ENUM.d2j(clazz, value);
        }
        return value;
    }
}
