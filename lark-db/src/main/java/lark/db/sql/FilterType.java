package lark.db.sql;

/**
 * 条件类型
 *
 * @author cuigh
 */
public enum FilterType {
    EQ("="), NE("<>"), LT("<"), GT(">"), LTE("<="), GTE(">="), IN("IN"), NIN("NOT IN"), LK("LIKE");

    String value;

    FilterType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
