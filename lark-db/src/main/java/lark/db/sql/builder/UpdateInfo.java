package lark.db.sql.builder;

import lark.db.sql.Updaters;
import lark.db.sql.Filter;

/**
 * @author cuigh
 */
public interface UpdateInfo {
    String getTable();

    Filter getWhere();

    Updaters getValues();
}
