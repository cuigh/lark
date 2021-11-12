package lark.db.sql.clause;

import lark.db.sql.Filter;

/**
 * @author cuigh
 */
public interface DeleteClause {
    DeleteEndClause where(Filter filter);
}
