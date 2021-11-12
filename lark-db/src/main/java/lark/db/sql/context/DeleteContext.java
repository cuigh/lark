package lark.db.sql.context;

import lark.db.sql.Filter;
import lark.db.sql.builder.BuildResult;
import lark.db.sql.builder.Builder;
import lark.db.sql.builder.DeleteInfo;
import lark.db.sql.clause.DeleteClause;
import lark.db.sql.clause.DeleteEndClause;
import lark.db.sql.mapper.MapperFactory;
import org.springframework.jdbc.core.JdbcOperations;

/**
 * @author cuigh
 */
public class DeleteContext extends BaseContext<DeleteContext> implements DeleteInfo, DeleteClause, DeleteEndClause {
    private String table;
    private Filter where;

    public DeleteContext(JdbcOperations operations, Builder builder, MapperFactory mapperFactory) {
        super(operations, builder, mapperFactory);
    }

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public Filter getWhere() {
        return where;
    }

    public void reset(String table) {
        this.table = table;
        this.where = null;
    }

    @Override
    public DeleteEndClause where(Filter filter) {
        this.where = filter;
        return this;
    }

    @Override
    public int submit() {
        BuildResult result = this.builder.buildDelete(this);
        log(result);
        return operations.update(result.getSql(), result.getArgs());
    }
}
