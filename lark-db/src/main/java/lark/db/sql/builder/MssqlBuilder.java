package lark.db.sql.builder;

import lark.db.sql.LockMode;
import lark.db.sql.SqlException;
import lark.db.sql.context.SelectContext;
import org.springframework.util.StringUtils;

/**
 * SQL Server 语句生成器
 *
 * @author cuigh
 */
public class MssqlBuilder extends BaseBuilder {
    @Override
    protected void quote(BuildBuffer buffer, String name) {
        buffer.addSql("[").addSql(name).addSql("]");
    }

    @Override
    protected void limit(BuildBuffer buffer, int skip, int take) {
        buffer.addSql(" OFFSET ").addSql(Integer.toString(skip)).addSql(" ROWS FETCH NEXT ")
                .addSql(Integer.toString(take)).addSql(" ROWS ONLY");
    }

    @Override
    protected void like(BuildBuffer buffer, String value) {
        buffer.addSql(" LIKE '%%' + ? + '%%'").addArg(value);
    }

    @Override
    protected void lock(BuildBuffer buffer, LockMode mode) {
        if (mode == LockMode.SHARED) {
            buffer.addSql(" WITH(HOLDLOCK)");
        } else if (mode == LockMode.EXCLUSIVE) {
            buffer.addSql(" WITH(UPDLOCK)");
        } else {
            buffer.addSql(" WITH(NOLOCK)");
        }
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
        buffer.addSql(" FROM [%s]", info.getTable().getName());
        String alias = info.getTable().getAlias();
        if (!StringUtils.isEmpty(alias)) {
            buffer.addSql(" AS %s", alias);
        }
        lock(buffer, info.getLock());

        // JOIN
        if (info.getJoins() != null) {
            for (SelectContext.Joiner joiner : info.getJoins()) {
                buffer.addSql(" %s [%s]", joiner.getType().value(), joiner.getTable().getName());
                alias = joiner.getTable().getAlias();
                if (!StringUtils.isEmpty(alias)) {
                    buffer.addSql(" AS %s", alias);
                }
                lock(buffer, info.getLock());
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

        return buffer.getResult();
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
}
