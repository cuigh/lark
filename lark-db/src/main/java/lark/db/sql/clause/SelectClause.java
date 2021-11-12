package lark.db.sql.clause;

import lark.db.sql.Table;

/**
 * @author cuigh
 */
public interface SelectClause {
    FromClause from(Table table);

    FromClause from(String table);
}
