package lark.db.sql;

/**
 * 更新类型
 *
 * @author cuigh
 */
public enum UpdateType {
    /**
     * 等于
     */
    EQ("="),

    /**
     * 增加
     */
    INC("+"),

    /**
     * 减少
     */
    DEC("-"),

    /**
     * 表达式
     */
    XP("");

    private String value;

    UpdateType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
