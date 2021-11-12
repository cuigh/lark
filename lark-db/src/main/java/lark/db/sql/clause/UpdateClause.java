package lark.db.sql.clause;

import lark.db.sql.Updaters;

/**
 * @author cuigh
 */
public interface UpdateClause {
    SetClause set(Updaters values);
}
