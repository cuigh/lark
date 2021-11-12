package lark.db.sql;

import org.springframework.util.StringUtils;

/**
 * @author cuigh
 */
public abstract class Table {
    public static Table create(String name) {
        return new SimpleTable(name, null);
    }

    public static Table create(String name, String alias) {
        return new SimpleTable(name, alias);
    }

    public abstract String getName();

    public abstract String getAlias();

    public abstract String getPrefix();

    public abstract Columns columns(String... cols);

    public abstract Groupers groupers(String... cols);

    public abstract Sorters asc(String... cols);

    public abstract Sorters desc(String... cols);

    static class SimpleTable extends Table {
        private String name;
        private String alias;

        SimpleTable(String name, String alias) {
            this.name = name;
            this.alias = alias;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getAlias() {
            return this.alias;
        }

        @Override
        public String getPrefix() {
            return StringUtils.isEmpty(alias) ? name : alias;
        }

        @Override
        public Columns columns(String... cols) {
            Columns columns = new Columns();
            columns.add(this, cols);
            return columns;
        }

        @Override
        public Groupers groupers(String... cols) {
            return new Groupers(this, cols);
        }

        @Override
        public Sorters asc(String... cols) {
            return new Sorters().asc(cols);
        }

        @Override
        public Sorters desc(String... cols) {
            return new Sorters().desc(cols);
        }
    }
}
