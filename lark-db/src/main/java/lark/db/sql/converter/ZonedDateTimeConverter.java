package lark.db.sql.converter;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Created by noname on 15/11/20.
 */
public class ZonedDateTimeConverter implements Converter<ZonedDateTime> {
    @Override
    public Object j2d(ZonedDateTime value) {
        return Timestamp.from(value.toInstant());
    }

    @Override
    public ZonedDateTime d2j(Class<ZonedDateTime> type, Object value) {
        Timestamp ts = (Timestamp) value;
        return ZonedDateTime.ofInstant(ts.toInstant(), ZoneOffset.UTC);
    }
}
