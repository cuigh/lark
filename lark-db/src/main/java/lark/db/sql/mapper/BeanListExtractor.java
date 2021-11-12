package lark.db.sql.mapper;

import lark.core.util.Exceptions;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @param <T> entity type
 * @author cuigh
 */
public class BeanListExtractor<T> implements ResultSetExtractor<List<T>> {
    private final Mapper<T> mapper;

    public BeanListExtractor(Mapper<T> mapper) {
        this.mapper = mapper;
    }


    @Override
    public List<T> extractData(ResultSet rs) throws SQLException {
        List<T> list = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        try {
            while (rs.next()) {
                T item = mapper.getType().getDeclaredConstructor().newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    Object value = rs.getObject(i);
                    if (value != null) {
                        mapper.setValue(item, metaData.getColumnLabel(i), value);
                    }
                }
                list.add(item);
            }
        } catch (Exception e) {
            throw Exceptions.asRuntime(e);
        }
        return list;
    }
}
