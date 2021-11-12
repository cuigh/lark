package lark.db.sql.context;

import lark.db.sql.builder.Builder;
import lark.db.sql.clause.ExecuteEndClause;
import lark.db.sql.mapper.BeanListExtractor;
import lark.db.sql.mapper.MapperFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * SQL语句执行上下文
 *
 * @author cuigh
 */
public class ExecuteContext extends BaseContext<ExecuteContext> implements ExecuteEndClause {
    private String sql;
    private Object[] args;

    public ExecuteContext(JdbcOperations operations, Builder builder, MapperFactory mapperFactory) {
        super(operations, builder, mapperFactory);
    }

    @Override
    public <T> T value(Class<T> type) {
        return operations.queryForObject(sql, args, type);
    }

    @Override
    public Map<String, Object> one() {
        List<Map<String, Object>> list = operations.query(sql, args, new RowMapperResultSetExtractor<>(new ColumnMapRowMapper()));
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
//        return operations.queryForMap(sql, args);
    }

    @Override
    public <T> T one(Class<T> type) {
        List<T> list = list(type);
        return list.isEmpty() ? null : list.get(0);
//        return operations.queryForObject(sql, args, mapperFactory.get(type));
//        return operations.queryForObject(sql, args, BeanPropertyRowMapper.newInstance(type));
    }

    @Override
    public <T> List<T> list(Class<T> type) {
        BeanListExtractor<T> extractor = new BeanListExtractor<>(mapperFactory.get(type));
        return operations.query(sql, args, extractor);
//        return operations.query(sql, args, BeanPropertyRowMapper.newInstance(type));
    }

    @Override
    public int submit() {
        return operations.update(sql, args);
    }

    public void reset(String sql, Object[] args) {
        this.sql = sql;
        this.args = args;
    }
}
