package lark.db.sql.builder;

import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author cuigh
 */
public class BuildResult {
    private String sql;
    private Object[] args;

    public BuildResult(String sql, Object[] args) {
        this.sql = sql;
        this.args = args;
    }

    public BuildResult(String sql, List<Object> args) {
        this.sql = sql;
        this.args = CollectionUtils.isEmpty(args) ? null : args.toArray();
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return String.format("sql: %s, args: %s", sql, Arrays.toString(args));
    }
}
