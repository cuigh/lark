package lark.db.sql.clause;

import lark.db.sql.Filter;

/**
 * @author cuigh
 */
public interface SetClause extends UpdateEndClause {
    UpdateEndClause where(Filter filter);
}
