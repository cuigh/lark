package lark.db.sql;

/**
 * @author cuigh
 */
public interface Column {
    /**
     * 获取列的别名
     *
     * @return 别名
     */
    String getAlias();

    /**
     * 常规数据列
     */
    class SimpleColumn implements Column {
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
    class PolyColumn implements Column {
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
    class ExprColumn implements Column {
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
