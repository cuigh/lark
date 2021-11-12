package lark.db.sql;

/**
 * @author cuigh
 */
public enum SortType {
    /**
     * Sort by asc
     */
    ASC("ASC"),

    /**
     * Sort by desc
     */
    DESC("DESC");

    String value;

    SortType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
