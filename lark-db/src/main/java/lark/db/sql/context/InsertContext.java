package lark.db.sql.context;

import lark.db.sql.InsertResult;
import lark.db.sql.builder.BuildResult;
import lark.db.sql.builder.Builder;
import lark.db.sql.builder.InsertInfo;
import lark.db.sql.clause.ColumnsClause;
import lark.db.sql.clause.InsertClause;
import lark.db.sql.clause.InsertEndClause;
import lark.db.sql.clause.ValuesClause;
import lark.db.sql.mapper.MapperFactory;
import org.springframework.jdbc.core.JdbcOperations;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cuigh
 */
public class InsertContext extends BaseContext<InsertContext> implements InsertInfo, InsertClause, ColumnsClause, ValuesClause, InsertEndClause {
    private String table;
    private String[] columns;
    private List<Object[]> values = new ArrayList<>(1);

    public InsertContext(JdbcOperations operations, Builder builder, MapperFactory mapperFactory) {
        super(operations, builder, mapperFactory);
    }

    public void reset(String table) {
        this.table = table;
        this.columns = null;
        this.values.clear();
    }

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public String[] getColumns() {
        return columns;
    }

    @Override
    public List<Object[]> getValues() {
        return values;
    }

    @Override
    public int submit() {
        BuildResult result = this.builder.buildInsert(this);
        log(result);
        return operations.update(result.getSql(), result.getArgs());
    }

    @Override
    public InsertResult result() {
        BuildResult result = this.builder.buildInsert(this);
        log(result);
        InsertResult holder = new InsertResult();
        int affectedRows = operations.update(conn -> {
            PreparedStatement statement = conn.prepareStatement(result.getSql(), Statement.RETURN_GENERATED_KEYS);
            Object[] args = result.getArgs();
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    statement.setObject(i + 1, args[i]);
                }
            }
            return statement;
        }, holder);
        holder.setAffectedRows(affectedRows);
        return holder;
    }

    @Override
    public ValuesClause values(Object... values) {
        this.values.add(values);
        return this;
    }

    @Override
    public ColumnsClause columns(String... columns) {
        this.columns = columns;
        return this;
    }

}
