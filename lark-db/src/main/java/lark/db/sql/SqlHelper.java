package lark.db.sql;

/**
 * 提供创建查询对象的快捷方式
 *
 * @author cuigh
 */
public final class SqlHelper {
    private SqlHelper() {
    }

    public static Criteria f() {
        return new Criteria();
    }

    public static Criteria f(String column, Object value) {
        return new Criteria(column, value);
    }

    public static Criteria f(Table table1, String column1, Table table2, String column2) {
        return new Criteria().eq(table1, column1, table2, column2);
    }

    public static Table t(String name) {
        return Table.create(name);
    }

    public static Table t(String name, String alias) {
        return Table.create(name, alias);
    }

    public static Updaters u() {
        return new Updaters();
    }

    public static Updaters u(String column, Object value) {
        return new Updaters(column, value);
    }

    /**
     * 分组子句快捷生成方法
     *
     * @param columns 列名列表
     * @return
     */
    public static Groupers g(String... columns) {
        return new Groupers(columns);
    }

    /**
     * 分组子句快捷生成方法
     *
     * @param table   表
     * @param columns 列名列表
     * @return
     */
    public static Groupers g(Table table, String... columns) {
        return new Groupers(table, columns);
    }

    /**
     * sort by asc
     *
     * @param columns 列名列表
     * @return
     */
    public static Sorters asc(String... columns) {
        return new Sorters().asc(columns);
    }

    /**
     * sort by asc
     *
     * @param table   表
     * @param columns 列名列表
     * @return
     */
    public static Sorters asc(Table table, String... columns) {
        return new Sorters().asc(table, columns);
    }

    /**
     * sort by desc
     *
     * @param columns 列名列表
     * @return
     */
    public static Sorters desc(String... columns) {
        return new Sorters().desc(columns);
    }

    /**
     * sort by desc
     *
     * @param table   表
     * @param columns 列名列表
     * @return
     */
    public static Sorters desc(Table table, String... columns) {
        return new Sorters().desc(table, columns);
    }

//    /**
//     * 存储过程参数快捷生成方法
//     *
//     * @return
//     */
//    public static CallParams p(int paramCount) {
//        return new CallParams(paramCount);
//    }

    /**
     * 查询列快捷生成方法
     *
     * @return
     */
    public static Columns c(String... columns) {
        return new Columns(null, columns);
    }

    /**
     * 查询列快捷生成方法
     *
     * @param table   表
     * @param columns 列名
     * @return
     */
    public static Columns c(Table table, String... columns) {
        return new Columns(table, columns);
    }

    /**
     * 查询列快捷生成方法
     *
     * @param expr  表达式, 如: COUNT(*)
     * @param alias 别名, 如: Count
     * @return
     */
    public static Columns ca(String expr, String alias) {
        return new Columns().add(expr, alias);
    }

    /**
     * 查询列快捷生成方法
     *
     * @param table   表
     * @param columns 列名列表, 逗号分割
     * @return
     */
    public static Columns cs(Table table, String columns) {
        return new Columns(table, columns.split(","));
    }

    /**
     * 查询列快捷生成方法
     *
     * @param table   表名
     * @param columns 列名列表, 逗号分割
     * @return
     */
    public static Columns cs(String table, String columns) {
        return new Columns(t(table), columns.split(","));
    }

    /**
     * 查询列快捷生成方法
     *
     * @param columns 列名列表, 逗号分割
     * @return
     */
    public static Columns cs(String columns) {
        return new Columns(null, columns.split(","));
    }

    /**
     * COUNT(0) AS count 查询列快捷生成方法, 等价于 c("COUNT(0)", "count")
     *
     * @return
     */
    public static Columns count() {
        return Columns.count();
    }
}
