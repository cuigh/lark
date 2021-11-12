package lark.db.sql;

/**
 * @author cuigh
 */
public class SqlException extends RuntimeException {
    public SqlException() {
        super();
    }

    public SqlException(String message) {
        super(message);
    }

    public SqlException(Throwable cause) {
        super(cause);
    }

    public SqlException(String message, Throwable cause) {
        super(message, cause);
    }
}
