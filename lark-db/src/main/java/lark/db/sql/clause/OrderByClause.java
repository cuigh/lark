package lark.db.sql.clause;

import lark.db.sql.LockMode;

/**
 * @author cuigh
 */
public interface OrderByClause extends SelectEndClause {
    SelectEndClause limit(int skip, int take);

    SelectEndClause page(int pageIndex, int pageSize);

    SelectEndClause lock(LockMode mode);
}
