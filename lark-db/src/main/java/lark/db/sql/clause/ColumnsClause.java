package lark.db.sql.clause;

/**
 * @author cuigh
 */
public interface ColumnsClause {
    /**
     * 设置要插入记录的数据值
     *
     * @param values 数据列值
     * @return
     */
    ValuesClause values(Object... values);
}
