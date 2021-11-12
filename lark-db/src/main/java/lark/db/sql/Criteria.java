package lark.db.sql;

import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cuigh
 */
public final class Criteria extends Filter {
    private List<FilterItem> items;

    public Criteria() {
    }

    public Criteria(String column, Object value) {
        this.eq(column, value);
    }

    public List<FilterItem> getItems() {
        return items;
    }

    public boolean hasItems() {
        return !CollectionUtils.isEmpty(items);
    }

    /**
     * 添加自定义表达式查询条件(慎用, 使用此方法将不能保证跨数据库兼容)
     *
     * @param expr 表达式, 如: SUM(COUNT)>100
     * @return Criteria
     */
    public Criteria xp(String expr) {
        this.add(new ExprFilterItem(expr));
        return this;
    }

    /**
     * Add '=' condition
     *
     * @param column 列
     * @param value  值
     * @return Criteria
     */
    public Criteria eq(String column, Object value) {
        this.add(new OneColumnFilterItem(column, value));
        return this;
    }

    /**
     * Add '=' condition
     *
     * @param when
     * @param column 列
     * @param value  值
     * @return
     */
    public Criteria eq(boolean when, String column, Object value) {
        if (when) {
            this.add(new OneColumnFilterItem(column, value));
        }
        return this;
    }

    public Criteria eq(Table table, String column, Object value) {
        this.add(new OneColumnFilterItem(table, column, FilterType.EQ, value));
        return this;
    }

    public Criteria eq(Table table1, String column1, Table table2, String column2) {
        this.add(new TwoColumnFilterItem(table1, column1, FilterType.EQ, table2, column2));
        return this;
    }

    /**
     * Add '!=' condition
     *
     * @param column 列
     * @param value  值
     * @return Criteria
     */
    public Criteria ne(String column, Object value) {
        this.add(new OneColumnFilterItem(column, FilterType.NE, value));
        return this;
    }

    public Criteria ne(Table table, String column, Object value) {
        this.add(new OneColumnFilterItem(table, column, FilterType.NE, value));
        return this;
    }

    public Criteria ne(Table table1, String column1, Table table2, String column2) {
        this.add(new TwoColumnFilterItem(table1, column1, FilterType.NE, table2, column2));
        return this;
    }


    /**
     * Add '<' condition
     *
     * @param column 列
     * @param value  值
     * @return Criteria
     */
    public Criteria lt(String column, Object value) {
        this.add(new OneColumnFilterItem(column, FilterType.LT, value));
        return this;
    }

    public Criteria lt(Table table, String column, Object value) {
        this.add(new OneColumnFilterItem(table, column, FilterType.LT, value));
        return this;
    }

    public Criteria lt(Table table1, String column1, Table table2, String column2) {
        this.add(new TwoColumnFilterItem(table1, column1, FilterType.LT, table2, column2));
        return this;
    }

    /**
     * Add '>' condition
     *
     * @param column 列
     * @param value  值
     * @return Criteria
     */
    public Criteria gt(String column, Object value) {
        this.add(new OneColumnFilterItem(column, FilterType.GT, value));
        return this;
    }

    public Criteria gt(Table table, String column, Object value) {
        this.add(new OneColumnFilterItem(table, column, FilterType.GT, value));
        return this;
    }

    public Criteria gt(Table table1, String column1, Table table2, String column2) {
        this.add(new TwoColumnFilterItem(table1, column1, FilterType.GT, table2, column2));
        return this;
    }

    /**
     * Add '<=' condition
     *
     * @param column 列
     * @param value  值
     * @return Criteria
     */
    public Criteria lte(String column, Object value) {
        this.add(new OneColumnFilterItem(column, FilterType.LTE, value));
        return this;
    }

    public Criteria lte(Table table, String column, Object value) {
        this.add(new OneColumnFilterItem(table, column, FilterType.LTE, value));
        return this;
    }

    public Criteria lte(Table table1, String column1, Table table2, String column2) {
        this.add(new TwoColumnFilterItem(table1, column1, FilterType.LTE, table2, column2));
        return this;
    }

    /**
     * Add '>=' condition
     *
     * @param column 列
     * @param value  值
     * @return Criteria
     */
    public Criteria gte(String column, Object value) {
        this.add(new OneColumnFilterItem(column, FilterType.GTE, value));
        return this;
    }

    public Criteria gte(Table table, String column, Object value) {
        this.add(new OneColumnFilterItem(table, column, FilterType.GTE, value));
        return this;
    }

    public Criteria gte(Table table1, String column1, Table table2, String column2) {
        this.add(new TwoColumnFilterItem(table1, column1, FilterType.GTE, table2, column2));
        return this;
    }

    /**
     * Add 'IN' condition
     *
     * @param column 列
     * @param value  值
     * @return Criteria
     */
    public Criteria in(String column, Object value) {
        this.add(new OneColumnFilterItem(column, FilterType.IN, value));
        return this;
    }

    public Criteria in(Table table, String column, Object value) {
        this.add(new OneColumnFilterItem(table, column, FilterType.IN, value));
        return this;
    }

    /**
     * Add 'NOT IN' condition
     *
     * @param column 列
     * @param value  值
     * @return Criteria
     */
    public Criteria nin(String column, Object value) {
        this.add(new OneColumnFilterItem(column, FilterType.NIN, value));
        return this;
    }

    public Criteria nin(Table table, String column, Object value) {
        this.add(new OneColumnFilterItem(table, column, FilterType.NIN, value));
        return this;
    }

    /**
     * Add 'LIKE' condition
     *
     * @param column 列
     * @param value  值
     * @return Criteria
     */
    public Criteria like(String column, String value) {
        this.add(new OneColumnFilterItem(column, FilterType.LK, value));
        return this;
    }

    public Criteria like(Table table, String column, String value) {
        this.add(new OneColumnFilterItem(table, column, FilterType.LK, value));
        return this;
    }

    private void add(FilterItem item) {
        if (this.items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
    }

    /**
     * 条件项类型
     */
    public enum FilterItemType {
        /**
         * 表达式条件，如：a is null
         */
        EXPR,
        /**
         * 单列条件，如：a = 1
         */
        ONE_COLUMN,
        /**
         * 双列条件，如：a = b
         */
        TWO_COLUMN;
    }

    public interface FilterItem {
        FilterItemType getItemType();
    }

    @Getter
    public static class OneColumnFilterItem implements FilterItem {
        private Table table;
        private FilterType type;
        private String column;
        private Object value;

        OneColumnFilterItem(String col, Object val) {
            this.column = col;
            this.type = FilterType.EQ;
            this.value = val;
        }

        OneColumnFilterItem(String col, FilterType type, Object val) {
            this.column = col;
            this.type = type;
            this.value = val;
        }

        OneColumnFilterItem(Table t, String col, FilterType type, Object val) {
            this.table = t;
            this.column = col;
            this.type = type;
            this.value = val;
        }

        @Override
        public FilterItemType getItemType() {
            return FilterItemType.ONE_COLUMN;
        }
    }

    @Getter
    public static class TwoColumnFilterItem implements FilterItem {
        private Table table1;
        private String column1;
        private FilterType type;
        private Table table2;
        private String column2;

        TwoColumnFilterItem(Table t1, String col1, FilterType ft, Table t2, String col2) {
            this.table1 = t1;
            this.column1 = col1;
            this.type = ft;
            this.table2 = t2;
            this.column2 = col2;
        }

        @Override
        public FilterItemType getItemType() {
            return FilterItemType.TWO_COLUMN;
        }
    }

    @Getter
    public static class ExprFilterItem implements FilterItem {
        private String expr;

        ExprFilterItem(String expr) {
            this.expr = expr;
        }

        @Override
        public FilterItemType getItemType() {
            return FilterItemType.EXPR;
        }
    }
}
