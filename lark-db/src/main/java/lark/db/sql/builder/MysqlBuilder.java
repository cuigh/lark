package lark.db.sql.builder;

import lark.db.sql.LockMode;

/**
 * MySQL 语句生成器
 *
 * @author cuigh
 */
public class MysqlBuilder extends BaseBuilder {
    @Override
    protected void quote(BuildBuffer buffer, String name) {
        buffer.addSql("`").addSql(name).addSql("`");
    }

    @Override
    protected void limit(BuildBuffer buffer, int skip, int take) {
        buffer.addSql(" LIMIT ").addSql(Integer.toString(skip)).addSql(",").addSql(Integer.toString(take));
    }

    @Override
    protected void like(BuildBuffer buffer, String value) {
        buffer.addSql(" LIKE ?").addArg("%" + value + "%");
    }

    @Override
    protected void lock(BuildBuffer buffer, LockMode mode) {
        if (mode == LockMode.SHARED) {
            buffer.addSql(" lock in share mode");
        } else if (mode == LockMode.EXCLUSIVE) {
            buffer.addSql(" for update");
        }
    }
}
