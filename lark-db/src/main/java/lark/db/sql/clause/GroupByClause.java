package lark.db.sql.clause;

import lark.db.sql.Filter;
import lark.db.sql.LockMode;
import lark.db.sql.Sorters;

/**
 * @author cuigh
 */
public interface GroupByClause extends SelectEndClause {
    SelectEndClause limit(int skip, int take);

    SelectEndClause page(int pageIndex, int pageSize);

    SelectEndClause lock(LockMode mode);

    OrderByClause orderBy(Sorters sorters);

    HavingClause having(Filter f);
}
