package lark.db.sql.builder;

import lark.db.sql.Filter;

/**
 * @author cuigh
 */
public interface DeleteInfo {
    String getTable();

    Filter getWhere();
}
