package lark.db.sql.clause;

/**
 * @author cuigh
 */
public interface ValuesClause extends InsertEndClause {
    ValuesClause values(Object... values);
}
