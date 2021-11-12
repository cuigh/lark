package lark.db.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cuigh
 */
public final class Columns {
    List<Column> list = new ArrayList<>();

    public Columns() {
        // default ctor
    }

    public Columns(Table t, String... cols) {
        this.add(t, cols);
    }

    public static Columns count() {
        return new Columns().add("COUNT(0)", "count");
    }

    public List<Column> getList() {
        return list;
    }

    /**
     * add table columns
     *
     * @param t    table
     * @param cols columns
     * @return
     */
    public Columns add(Table t, String... cols) {
        for (String col : cols) {
            list.add(new SimpleColumn(t, col, null));
        }
        return this;
    }

    /**
     * add table columns
     *
     * @param t   table
     * @param col column
     * @return
     */
    public Columns add(Table t, String col) {
        list.add(new SimpleColumn(t, col, null));
        return this;
    }

    /**
     * add expression column, like 'COUNT(*)'
     *
     * @param expr  expression
     * @param alias alias of the column
     * @return
     */
    public Columns add(String expr, String alias) {
        list.add(new ExprColumn(expr, alias));
        return this;
    }

    /**
     * add table column with alias
     *
     * @param t     table
     * @param col   column
     * @param alias alias of the column
     * @return
     */
    public Columns append(Table t, String col, String alias) {
        list.add(new SimpleColumn(t, col, alias));
        return this;
    }

    /**
     * 列
     */
    public interface Column {
        /**
         * 获取别名
         *
         * @return 别名
         */
        String getAlias();
    }

    /**
     * 常规数据列
     */
    public static class SimpleColumn implements Column {
        Table table;
        String column;
        String alias;

        SimpleColumn(Table table, String column, String alias) {
            this.table = table;
            this.column = column;
            this.alias = alias;
        }

        public Table getTable() {
            return table;
        }

        public String getColumn() {
            return column;
        }

        @Override
        public String getAlias() {
            return alias;
        }
    }

    /**
     * 聚合列
     */
    public static class PolyColumn implements Column {
        Table table;
        String column;
        String function;
        String alias;

        @Override
        public String getAlias() {
            return alias;
        }
    }

    /**
     * 表达式列
     */
    public static class ExprColumn implements Column {
        String expr;
        String alias;

        ExprColumn(String expr, String alias) {
            this.expr = expr;
            this.alias = alias;
        }

        public String getExpr() {
            return expr;
        }

        @Override
        public String getAlias() {
            return alias;
        }
    }
}
