package lark.db.sql.builder;

import java.util.List;

/**
 * @author cuigh
 */
public interface InsertInfo {
    String getTable();

    String[] getColumns();

    List<Object[]> getValues();
}
