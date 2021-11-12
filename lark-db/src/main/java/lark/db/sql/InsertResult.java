package lark.db.sql;

import org.springframework.jdbc.support.GeneratedKeyHolder;

/**
 * @author cuigh
 */
public class InsertResult extends GeneratedKeyHolder {
    private int affectedRows;

    public int getAffectedRows() {
        return affectedRows;
    }

    public void setAffectedRows(int affectedRows) {
        this.affectedRows = affectedRows;
    }
}
