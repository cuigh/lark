package lark.db.sql.context;

import lark.db.sql.Filter;
import lark.db.sql.Updaters;
import lark.db.sql.builder.BuildResult;
import lark.db.sql.builder.Builder;
import lark.db.sql.builder.UpdateInfo;
import lark.db.sql.clause.SetClause;
import lark.db.sql.clause.UpdateClause;
import lark.db.sql.clause.UpdateEndClause;
import lark.db.sql.mapper.MapperFactory;
import org.springframework.jdbc.core.JdbcOperations;

/**
 * @author cuigh
 */
public class UpdateContext extends BaseContext<UpdateContext> implements UpdateInfo, UpdateClause, SetClause, UpdateEndClause {
    private String table;
    private Filter where;
    private Updaters values = new Updaters();

    public UpdateContext(JdbcOperations operations, Builder builder, MapperFactory mapperFactory) {
        super(operations, builder, mapperFactory);
    }

    @Override
    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    @Override
    public Filter getWhere() {
        return where;
    }

    @Override
    public Updaters getValues() {
        return values;
    }

    public void reset(String table) {
        this.table = table;
        this.where = null;
        this.values.clear();
    }

    @Override
    public UpdateEndClause where(Filter filter) {
        this.where = filter;
        return this;
    }

    @Override
    public SetClause set(Updaters values) {
        this.values = values;
        return this;
    }

    @Override
    public int submit() {
        BuildResult result = this.builder.buildUpdate(this);
        log(result);
        return operations.update(result.getSql(), result.getArgs());
    }
}
