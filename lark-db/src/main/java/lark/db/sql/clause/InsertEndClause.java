package lark.db.sql.clause;

import lark.db.sql.InsertResult;

/**
 * @author cuigh
 */
public interface InsertEndClause {
    int submit();

    InsertResult result();
}
