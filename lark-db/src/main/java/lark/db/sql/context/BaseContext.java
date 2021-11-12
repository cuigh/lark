package lark.db.sql.context;

import lark.core.lang.Linkable;
import lark.db.sql.builder.BuildResult;
import lark.db.sql.builder.Builder;
import lark.db.sql.mapper.MapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;

/**
 * @author cuigh
 */
public abstract class BaseContext<T extends BaseContext<T>> implements Linkable<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseContext.class);
    protected final JdbcOperations operations;
    protected final Builder builder;
    protected final MapperFactory mapperFactory;
    private T next;

    protected BaseContext(JdbcOperations operations, Builder builder, MapperFactory mapperFactory) {
        this.operations = operations;
        this.builder = builder;
        this.mapperFactory = mapperFactory;
    }

    @Override
    public T getNext() {
        return next;
    }

    @Override
    public void setNext(T next) {
        this.next = next;
    }

    static void log(BuildResult result) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.debug("sql: {}, args: {}", result.getSql(), result.getArgs());
        }
    }

    static void log(String sql, Object[] args) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.debug("sql: {}, args: {}", sql, args);
        }
    }
}
