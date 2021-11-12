package lark.db.sql.converter;

import lark.core.lang.EnumValuable;
import lark.core.util.Enums;

/**
 * Created by noname on 15/11/20.
 */
public class EnumConverter<T extends Enum & EnumValuable> implements Converter<T> {
    @Override
    public Object j2d(T value) {
        return value.value();
    }

    @Override
    public T d2j(Class<T> type, Object value) {
        return Enums.valueOf(type, (int) value);
    }
}
