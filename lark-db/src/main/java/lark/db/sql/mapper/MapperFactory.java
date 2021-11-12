package lark.db.sql.mapper;

import lark.db.sql.SqlException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对象映射辅助类工厂
 *
 * @author cuigh
 */
public final class MapperFactory {
    private final Map<Class<?>, Mapper> mappers = new ConcurrentHashMap<>();
    private final MapperOptions options;

    public MapperFactory(MapperOptions options) {
        this.options = (options == null) ? new MapperOptions() : options;
    }

    @SuppressWarnings("unchecked")
    public <T> Mapper<T> get(Class<T> clazz) {
        return (Mapper<T>) mappers.computeIfAbsent(clazz, cls -> {
            Mapper<T> m = new Mapper(cls, options);
            if (m.fields.isEmpty()) {
                throw new SqlException("clazz is not a valid entity: " + cls.getName());
            }
            return m;
        });
    }
}
