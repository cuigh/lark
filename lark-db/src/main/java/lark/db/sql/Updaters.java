package lark.db.sql;

import lombok.Getter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author cuigh
 */
public class Updaters {
    private HashMap<String, Updater> items = new HashMap<>();

    public Updaters() {
        // default ctor
    }

    public Updaters(String column, Object value) {
        this.eq(column, value);
    }

    /**
     * Set column value
     *
     * @param column column name
     * @param value  column value
     * @return
     */
    public Updaters eq(String column, Object value) {
        return this.add(column, UpdateType.EQ, value);
    }

    /**
     * Increase column value
     *
     * @param column column name
     * @param value  column value
     * @return
     */
    public Updaters inc(String column, Object value) {
        return this.add(column, UpdateType.INC, value);
    }

    /**
     * Decrease column value
     *
     * @param column column name
     * @param value  column value
     * @return
     */
    public Updaters dec(String column, Object value) {
        return this.add(column, UpdateType.DEC, value);
    }

    /**
     * Set column value with expression
     *
     * @param column column name
     * @param value  column value
     * @return
     */
    public Updaters xp(String column, String value) {
        return this.add(column, UpdateType.XP, value);
    }

    private Updaters add(String column, UpdateType type, Object value) {
        this.items.put(column, new Updater(type, value));
        return this;
    }

    public HashMap<String, Updater> getItems() {
        return items;
    }

    public void clear() {
        this.items.clear();
    }

    /**
     * Update type and value holder
     */
    public static class Updater {
        @Getter
        private UpdateType type;
        @Getter
        private Object value;

        private Updater(UpdateType type, Object value) {
            this.type = type;
            this.value = value;
        }
    }
}
