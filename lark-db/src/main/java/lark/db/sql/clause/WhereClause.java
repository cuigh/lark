package lark.db.sql.clause;

import lark.db.sql.Groupers;
import lark.db.sql.LockMode;
import lark.db.sql.Sorters;

/**
 * @author cuigh
 */
public interface WhereClause extends SelectEndClause {
    SelectEndClause limit(int skip, int take);

    SelectEndClause page(int pageIndex, int pageSize);

    SelectEndClause lock(LockMode mode);

    GroupByClause groupBy(Groupers groupers);

    OrderByClause orderBy(Sorters sorters);
}
