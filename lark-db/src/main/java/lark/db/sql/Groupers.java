package lark.db.sql;

import lark.core.util.Arrays;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cuigh
 */
public final class Groupers {
    private List<Grouper> items;

    public Groupers(String... columns) {
        this.add(null, columns);
    }

    public Groupers(Table table, String... columns) {
        this.add(table, columns);
    }

    public List<Grouper> getItems() {
        return items;
    }

    public Groupers add(String... columns) {
        return this.add(null, columns);
    }

    public Groupers add(Table table, String... columns) {
        if (!Arrays.isEmpty(columns)) {
            if (this.items == null) {
                this.items = new ArrayList<>();
            }
            Grouper grouper = new Grouper(table, columns);
            this.items.add(grouper);
        }
        return this;
    }

    /**
     * Group item
     */
    public static class Grouper {
        Table table;
        String[] columns;

        Grouper(Table table, String[] columns) {
            this.table = table;
            this.columns = columns;
        }

        public Table getTable() {
            return table;
        }

        public String[] getColumns() {
            return columns;
        }
    }
}
