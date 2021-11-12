package lark.db.sql.builder;

import lark.db.sql.DatabaseType;
import lark.db.sql.SqlException;

/**
 * @author cuigh
 */
public interface Builder {
    /**
     * 创建 SQL Builder 实例
     *
     * @param type database type
     * @return Builder 实例
     */
    static Builder create(DatabaseType type) {
        switch (type) {
            case MYSQL:
                return new MysqlBuilder();
            case SQLSERVER:
                return new MssqlBuilder();
            default:
                throw new SqlException("unknown database type: " + type);
        }
    }

    /**
     * Build INSERT SQL
     *
     * @param info context information
     * @return SQL clause and arguments
     */
    BuildResult buildInsert(InsertInfo info);

    /**
     * Build SELECT SQL
     *
     * @param info context information
     * @return SQL clause and arguments
     */
    BuildResult buildSelect(SelectInfo info);

    /**
     * Build UPDATE SQL
     *
     * @param info context information
     * @return SQL clause and arguments
     */
    BuildResult buildUpdate(UpdateInfo info);

    /**
     * Build DELETE SQL
     *
     * @param info context information
     * @return SQL clause and arguments
     */
    BuildResult buildDelete(DeleteInfo info);
}
