package lark.db.sql.clause;

import java.util.List;
import java.util.Map;

/**
 * @author cuigh
 */
public interface SelectEndClause {
    /**
     * 读取第一行记录的第一列, 并在读取后关闭数据库资源
     *
     * @return
     */
    <T> T value(Class<T> type);

    /**
     * 读取第一行记录, 并在读取后关闭数据库资源
     *
     * @return
     */
    Map<String, Object> one();

    /**
     * 读取一行记录转换为指定的数据实体, 并在读取后关闭数据库资源
     *
     * @param type
     * @param <T>
     * @return
     */
    <T> T one(Class<T> type);

    /**
     * 读取所有记录转换为指定的数据实体, 并在读取所有数据后关闭数据库资源
     *
     * @param type
     * @param <T>
     * @return
     */
    <T> List<T> list(Class<T> type);
}
