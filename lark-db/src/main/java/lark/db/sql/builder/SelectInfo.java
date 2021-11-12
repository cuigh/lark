package lark.db.sql.builder;

import lark.db.sql.LockMode;
import lark.db.sql.*;
import lark.db.sql.context.SelectContext;

import java.util.List;

/**
 * @author cuigh
 */
public interface SelectInfo {
    boolean isDistinct();

    LockMode getLock();

    List<Columns.Column> getColumns();

    List<SelectContext.Joiner> getJoins();

    Filter getWhere();

    List<Groupers.Grouper> getGroups();

    Filter getHaving();

    List<Sorters.Sorter> getOrders();

    int getSkip();

    int getTake();

    Table getTable();
}
