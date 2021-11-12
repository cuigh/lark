package lark.db.sql;

/**
 * 锁模式
 *
 * @author cuigh
 */
public enum LockMode {
    NONE(), SHARED(), EXCLUSIVE()
}