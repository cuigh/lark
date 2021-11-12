package lark.db.sql.builder;

import lark.core.util.Arrays;
import lark.db.sql.*;
import lark.db.sql.context.SelectContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * MySQL 语句生成器
 *
 * @author cuigh
 */
public abstract class BaseBuilder implements Builder {
    private static final String[] INSERT_VALUE_ARGS = new String[]{
            "(?)",
            "(?,?)",
            "(?,?,?)",
            "(?,?,?,?)",
            "(?,?,?,?,?)",
            "(?,?,?,?,?,?)",
            "(?,?,?,?,?,?,?)",
            "(?,?,?,?,?,?,?,?)",
            "(?,?,?,?,?,?,?,?,?)",
            "(?,?,?,?,?,?,?,?,?,?)",
            "(?,?,?,?,?,?,?,?,?,?,?)",
            "(?,?,?,?,?,?,?,?,?,?,?,?)",
            "(?,?,?,?,?,?,?,?,?,?,?,?,?)",
            "(?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
            "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
            "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
            "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
            "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
            "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
            "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
    }; // 20

    protected abstract void quote(BuildBuffer buffer, String name);

    protected abstract void limit(BuildBuffer buffer, int skip, int take);

    protected abstract void like(BuildBuffer buffer, String value);

    protected abstract void lock(BuildBuffer buffer, LockMode mode);

    @Override
    public BuildResult buildInsert(InsertInfo info) {
        BuildBuffer buffer = new BuildBuffer();

        buffer.addSql("INSERT INTO ");
        quote(buffer, info.getTable());

        // columns
        String[] columns = info.getColumns();
        if (!Arrays.isEmpty(columns)) {
            buffer.addSql("(");
            for (int i = 0; i < columns.length; i++) {
                if (i > 0) {
                    buffer.addSql(",");
                }
                quote(buffer, columns[i]);
            }
            buffer.addSql(")");
        }

        // values
        buffer.addSql(" VALUES");
        List<Object[]> values = info.getValues();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                buffer.addSql(",");
            }

            Object[] args = values.get(i);
            buffer.addArg(args);
            if (columns.length > 20) {
                buffer.addSql("(").addSql(args.length, "?", ",").addSql(")");
            } else {
                buffer.addSql(INSERT_VALUE_ARGS[columns.length - 1]);
            }
        }

        return buffer.getResult();
    }

    @Override
    public BuildResult buildDelete(DeleteInfo info) {
        BuildBuffer buffer = new BuildBuffer();

        buffer.addSql("DELETE FROM ");
        quote(buffer, info.getTable());

        // where
        where(buffer, info.getWhere());

        return buffer.getResult();
    }

    @Override
    public BuildResult buildUpdate(UpdateInfo info) {
        BuildBuffer buffer = new BuildBuffer();

        buffer.addSql("UPDATE ");
        quote(buffer, info.getTable());
        buffer.addSql(" SET ");

        // columns
        Iterator<Map.Entry<String, Updaters.Updater>> iterator = info.getValues().getItems().entrySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            if (i++ > 0) {
                buffer.addSql(",");
            }

            Map.Entry<String, Updaters.Updater> entry = iterator.next();
            switch (entry.getValue().getType()) {
                case INC:
                    quote(buffer, entry.getKey());
                    buffer.addSql("=");
                    quote(buffer, entry.getKey());
                    buffer.addSql("+?").addArg(entry.getValue().getValue());
                    break;
                case DEC:
                    quote(buffer, entry.getKey());
                    buffer.addSql("=");
                    quote(buffer, entry.getKey());
                    buffer.addSql("-?").addArg(entry.getValue().getValue());
                    break;
                case XP:
                    quote(buffer, entry.getKey());
                    buffer.addSql("=%s", entry.getValue().getValue());
                    break;
                default:
                    quote(buffer, entry.getKey());
                    buffer.addSql("=?").addArg(entry.getValue().getValue());
                    break;
            }
        }

        // where
        where(buffer, info.getWhere());

        return buffer.getResult();
    }

    @Override
    public BuildResult buildSelect(SelectInfo info) {
        BuildBuffer buffer = new BuildBuffer();

        // SELECT
        buffer.addSql("SELECT ");
        if (info.isDistinct()) {
            buffer.addSql("DISTINCT ");
        }
        selectColumns(buffer, info.getColumns());

        // FROM
        String alias = info.getTable().getAlias();
        buffer.addSql(" FROM ");
        quote(buffer, info.getTable().getName());
        if (!StringUtils.isEmpty(alias)) {
            buffer.addSql(" AS ").addSql(alias);
        }

        // JOIN
        if (info.getJoins() != null) {
            for (SelectContext.Joiner joiner : info.getJoins()) {
                buffer.addSql(" %s ", joiner.getType().value());
                quote(buffer, joiner.getTable().getName());
                alias = joiner.getTable().getAlias();
                if (!StringUtils.isEmpty(alias)) {
                    buffer.addSql(" AS ").addSql(alias);
                }
                buffer.addSql(" ON ");
                BuildResult br = this.buildFilter(joiner.getOn());
                if (br == null) {
                    throw new SqlException("JOIN 语句后面必须有 ON 语句");
                }
                buffer.addSql(br.getSql());
                buffer.addArg(br.getArgs());
            }
        }

        // WHERE
        where(buffer, info.getWhere());

        // GROUP BY
        if (info.getGroups() != null) {
            selectGroupBy(buffer, info.getGroups(), info.getHaving());
        }

        // ORDER BY
        if (info.getOrders() != null) {
            selectOrderBy(buffer, info.getOrders());
        }

        // LIMIT
        if (info.getSkip() != 0 || info.getTake() != 0) {
            limit(buffer, info.getSkip(), info.getTake());
        }

        // LOCK
        lock(buffer, info.getLock());

        return buffer.getResult();
    }

    protected void where(BuildBuffer buffer, Filter where) {
        if (where != null) {
            BuildResult br = buildFilter(where);
            if (br != null) {
                buffer.addSql(" WHERE ");
                buffer.addSql(br.getSql());
                buffer.addArg(br.getArgs());
            }
        }
    }

    protected void selectGroupBy(BuildBuffer buffer, List<Groupers.Grouper> groupers, Filter having) {
        buffer.addSql(" GROUP BY ");
        for (int i = 0; i < groupers.size(); i++) {
            Groupers.Grouper g = groupers.get(i);
            if (i > 0) {
                buffer.addSql(",");
            }
            for (int j = 0; j < g.getColumns().length; j++) {
                if (j > 0) {
                    buffer.addSql(",");
                }
                if (g.getTable() != null) {
                    quote(buffer, g.getTable().getPrefix());
                    buffer.addSql(".");
                }
                quote(buffer, g.getColumns()[i]);
            }
        }

        if (having != null) {
            BuildResult br = this.buildFilter(having);
            if (br != null) {
                buffer.addSql(" HAVING ");
                buffer.addSql(br.getSql());
                buffer.addArg(br.getArgs());
            }
        }
    }

    protected void selectOrderBy(BuildBuffer buffer, List<Sorters.Sorter> sorters) {
        buffer.addSql(" ORDER BY ");
        for (int i = 0; i < sorters.size(); i++) {
            Sorters.Sorter order = sorters.get(i);
            if (i > 0) {
                buffer.addSql(",");
            }
            for (int j = 0; j < order.getColumns().length; j++) {
                if (j > 0) {
                    buffer.addSql(",");
                }
                if (order.getTable() != null) {
                    quote(buffer, order.getTable().getPrefix());
                    buffer.addSql(".");
                }
                quote(buffer, order.getColumns()[j]);
                buffer.addSql(" %s", order.getType().value());
            }
        }
    }

//    @Override
//    public String buildCall(CallContext.CallInfo info) {
//        StringBuilder sb = new StringBuilder("{call ");
//        sb.append(info.getSp());
//
//        CallArgs args = info.getParams();
//        if (args != null) {
//            sb.append('(');
//            int i = 0;
//            Iterator<Map.Entry<Integer, CallArgs.CallArg>> iter = args.iterator();
//            while (iter.hasNext()) {
//                Map.Entry<Integer, CallArgs.CallArg> entry = iter.next();
//                if (i > 0) {
//                    sb.append(',');
//                }
//                sb.append('?');
//            }
//            // todo:
//            sb.append(')');
//        }
//
//        sb.append("}");
//
//        return sb.toString();
//    }

    protected BuildResult buildFilter(Filter filter) {
        if (filter instanceof Criteria) {
            Criteria f = (Criteria) filter;
            if (!f.hasItems()) {
                return null;
            }

            BuildBuffer buffer = new BuildBuffer();
            List<Criteria.FilterItem> items = f.getItems();
            for (int i = 0; i < items.size(); i++) {
                if (i > 0) {
                    buffer.addSql(" AND ");
                }
                this.buildFilterItem(buffer, items.get(i));
            }
            return buffer.getResult();
        } else if (filter instanceof Filter.AndFilter) {
            Filter.AndFilter f = (Filter.AndFilter) filter;
            return combineFilter(f.getLeft(), f.getRight(), "AND");
        } else if (filter instanceof Filter.OrFilter) {
            Filter.OrFilter f = (Filter.OrFilter) filter;
            return combineFilter(f.getLeft(), f.getRight(), "OR");
        } else if (filter instanceof Filter.NotFilter) {
            BuildResult r = this.buildFilter(((Filter.NotFilter) filter).getInner());
            if (r != null) {
                r.setSql("NOT(" + r.getSql() + ")");
            }
        } else {
            throw new SqlException("unknown filter type: " + filter.getClass().getName());
        }
        return null;
    }

    protected void selectColumns(BuildBuffer buffer, List<Columns.Column> columns) {
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                buffer.addSql(",");
            }

            Columns.Column col = columns.get(i);
            if (col instanceof Columns.SimpleColumn) {
                Columns.SimpleColumn simpleColumn = (Columns.SimpleColumn) col;
                if (simpleColumn.getTable() != null) {
                    quote(buffer, simpleColumn.getTable().getPrefix());
                    buffer.addSql(".");
                }
                quote(buffer, simpleColumn.getColumn());
                if (!StringUtils.isEmpty(simpleColumn.getAlias())) {
                    buffer.addSql(" AS %s", simpleColumn.getAlias());
                }
            } else if (col instanceof Columns.ExprColumn) {
                Columns.ExprColumn exprColumn = (Columns.ExprColumn) col;
                buffer.addSql(exprColumn.getExpr());
                if (!StringUtils.isEmpty(exprColumn.getAlias())) {
                    buffer.addSql(" AS %s", exprColumn.getAlias());
                }
            } else if (col instanceof Columns.PolyColumn) {
                // todo:
            }
        }
    }

    private BuildResult combineFilter(Filter left, Filter right, String joiner) {
        BuildResult r1 = this.buildFilter(left);
        BuildResult r2 = this.buildFilter(right);
        if (r1 == null) {
            return r2;
        } else if (r2 == null) {
            return r1;
        } else {
            String sql = String.format("(%s) %s (%s)", r1.getSql(), joiner, r2.getSql());
            Object[] args;
            if (r1.getArgs() == null) {
                args = r2.getArgs();
            } else if (r2.getArgs() == null) {
                args = r1.getArgs();
            } else {
                args = new Object[r1.getArgs().length + r2.getArgs().length];
                System.arraycopy(r1.getArgs(), 0, args, 0, r1.getArgs().length);
                System.arraycopy(r2.getArgs(), 0, args, r1.getArgs().length, r2.getArgs().length);
            }
            return new BuildResult(sql, args);
        }
    }

    private void buildFilterItem(BuildBuffer buffer, Criteria.FilterItem item) {
        switch (item.getItemType()) {
            case ONE_COLUMN:
                this.buildOneColumnFilterItem(buffer, (Criteria.OneColumnFilterItem) item);
                break;
            case TWO_COLUMN:
                this.buildTwoColumnFilterItem(buffer, (Criteria.TwoColumnFilterItem) item);
                break;
            case EXPR:
                this.buildExprFilterItem(buffer, (Criteria.ExprFilterItem) item);
                break;
            default:
        }
    }

    private void buildOneColumnFilterItem(BuildBuffer buffer, Criteria.OneColumnFilterItem item) {
        Table t = item.getTable();
        if (t != null) {
            buffer.addSql("`%s`.", t.getPrefix());
        }

        quote(buffer, item.getColumn());
        switch (item.getType()) {
            case NE:
                if (item.getValue() == null) {
                    buffer.addSql(" IS NOT NULL");
                } else {
                    buffer.addSql("<>?").addArg(item.getValue());
                }
                break;
            case GT:
                buffer.addSql(">?").addArg(item.getValue());
                break;
            case GTE:
                buffer.addSql(">=?").addArg(item.getValue());
                break;
            case LT:
                buffer.addSql("<?").addArg(item.getValue());
                break;
            case LTE:
                buffer.addSql("<=?").addArg(item.getValue());
                break;
            case LK:
                like(buffer, (String) item.getValue());
                break;
            case IN:
            case NIN:
                buffer.addSql(" %s(%s)", item.getType().value(), buildInArgs(item.getValue()));
                break;
            default:
                if (item.getValue() == null) {
                    buffer.addSql(" IS NULL");
                } else {
                    buffer.addSql("=?").addArg(item.getValue());
                }
                break;
        }
    }

    private String buildInArgs(Object value) {
        Class<?> clazz = value.getClass();
        if (clazz.isArray()) {
            return joinArray(value, clazz.getComponentType());
        } else if (value instanceof Collection) {
            return joinCollection((Collection<?>) value);
        } else {
            return value.toString();
        }
    }

    private String joinArray(Object value, Class<?> elemType) {
        if (elemType == String.class) {
            return "'" + String.join("','", (String[]) value) + "'";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Array.getLength(value); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(Array.get(value, i));
        }
        return sb.toString();
    }

    private String joinCollection(Collection<?> value) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Object item : value) {
            if (i > 0) {
                sb.append(",");
            }
            if (item.getClass() == String.class) {
                sb.append("'");
                sb.append(item);
                sb.append("'");
            } else {
                sb.append(item);
            }
            i++;
        }
        return sb.toString();
    }

    private void buildTwoColumnFilterItem(BuildBuffer buffer, Criteria.TwoColumnFilterItem item) {
        switch (item.getType()) {
            case EQ:
            case NE:
            case LT:
            case GT:
            case LTE:
            case GTE:
                quote(buffer, item.getTable1().getPrefix());
                buffer.addSql(".");
                quote(buffer, item.getColumn1());
                buffer.addSql(item.getType().value());
                quote(buffer, item.getTable2().getPrefix());
                buffer.addSql(".");
                quote(buffer, item.getColumn2());
                break;
            default:
                throw new SqlException("invalid filter type: " + item.getType().value());
        }
    }

    private void buildExprFilterItem(BuildBuffer buffer, Criteria.ExprFilterItem item) {
        buffer.addSql(item.getExpr());
    }
}
