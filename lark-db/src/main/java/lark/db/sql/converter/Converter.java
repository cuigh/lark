package lark.db.sql.converter;

/**
 * Created by noname on 15/11/20.
 */
public interface Converter<T> {
    /**
     * Java 对象类型转换为数据库类型
     * @param value
     * @return
     */
    Object j2d(T value);

    /**
     * 数据库类型转换成 Java 对象类型
     * @param type
     * @param value
     * @return
     */
    T d2j(Class<T> type, Object value);
}
