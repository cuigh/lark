package lark.db.sql;

import lark.core.util.Arrays;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cuigh
 */
public final class Sorters {
    private List<Sorter> items;

    public List<Sorter> getItems() {
        return items;
    }

    public Sorters asc(String... columns) {
        return this.add(SortType.ASC, null, columns);
    }

    public Sorters asc(Table table, String... columns) {
        return this.add(SortType.ASC, null, columns);
    }

    public Sorters desc(String... columns) {
        return this.add(SortType.DESC, null, columns);
    }

    public Sorters desc(Table table, String... columns) {
        return this.add(SortType.DESC, null, columns);
    }

    private Sorters add(SortType type, Table table, String... columns) {
        if (!Arrays.isEmpty(columns)) {
            if (this.items == null) {
                this.items = new ArrayList<>();
            }
            Sorter sorter = new Sorter(type, table, columns);
            this.items.add(sorter);
        }
        return this;
    }

    /**
     * Sort item
     */
    public static class Sorter {
        SortType type;
        Table table;
        String[] columns;

        Sorter(SortType type, Table table, String[] columns) {
            this.type = type;
            this.table = table;
            this.columns = columns;
        }

        public SortType getType() {
            return type;
        }

        public Table getTable() {
            return table;
        }

        public String[] getColumns() {
            return columns;
        }
    }
}
