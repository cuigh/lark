package lark.db.sql.clause;

/**
 * @author cuigh
 */
public interface ExecuteEndClause extends SelectEndClause {
    int submit();
}
