package lark.db.sql.context;

import lark.db.sql.*;
import lark.db.sql.builder.BuildResult;
import lark.db.sql.builder.Builder;
import lark.db.sql.builder.SelectInfo;
import lark.db.sql.clause.*;
import lark.db.sql.mapper.BeanListExtractor;
import lark.db.sql.mapper.MapperFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author cuigh
 */
public class SelectContext extends BaseContext<SelectContext> implements SelectInfo, SelectClause, FromClause, WhereClause, GroupByClause, OrderByClause, HavingClause {
    private Table table;
    private boolean distinct;
    private LockMode lock = LockMode.NONE;
    private List<Columns.Column> columns;
    private List<Joiner> joins;
    private Filter where;
    private List<Groupers.Grouper> groups;
    private Filter having;
    private List<Sorters.Sorter> orders;
    private int skip;
    private int take;

    public SelectContext(JdbcOperations operations, Builder builder, MapperFactory mapperFactory) {
        super(operations, builder, mapperFactory);
    }

    public void reset(Columns columns) {
        this.table = null;
        this.distinct = false;
        this.lock = LockMode.NONE;
        this.columns = columns.getList();
        if (this.joins != null) {
            this.joins.clear();
        }
        this.where = null;
        if (this.groups != null) {
            this.groups.clear();
        }
        this.having = null;
        if (this.orders != null) {
            this.orders.clear();
        }
        this.skip = 0;
        this.take = 0;
    }

    @Override
    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public LockMode getLock() {
        return lock;
    }

    @Override
    public List<Columns.Column> getColumns() {
        return columns;
    }

    @Override
    public List<Joiner> getJoins() {
        return joins;
    }

    @Override
    public Filter getWhere() {
        return where;
    }

    @Override
    public List<Groupers.Grouper> getGroups() {
        return groups;
    }

    @Override
    public Filter getHaving() {
        return having;
    }

    @Override
    public List<Sorters.Sorter> getOrders() {
        return orders;
    }

    @Override
    public int getSkip() {
        return skip;
    }

    @Override
    public int getTake() {
        return take;
    }

    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public FromClause join(Table t, Filter on) {
        addJoiner(JoinType.INNER, t, on);
        return this;
    }

    @Override
    public FromClause join(String t, Filter on) {
        return this.join(Table.create(t), on);
    }

    @Override
    public FromClause leftJoin(Table t, Filter on) {
        addJoiner(JoinType.LEFT, t, on);
        return this;
    }

    @Override
    public FromClause leftJoin(String t, Filter on) {
        return this.leftJoin(Table.create(t), on);
    }

    @Override
    public FromClause rightJoin(Table t, Filter on) {
        addJoiner(JoinType.RIGHT, t, on);
        return this;
    }

    @Override
    public FromClause rightJoin(String t, Filter on) {
        return this.rightJoin(Table.create(t), on);
    }

    @Override
    public FromClause fullJoin(Table t, Filter on) {
        addJoiner(JoinType.FULL, t, on);
        return this;
    }

    @Override
    public FromClause fullJoin(String t, Filter on) {
        return this.fullJoin(Table.create(t), on);
    }

    private void addJoiner(JoinType type, Table t, Filter on) {
        if (this.joins == null) {
            this.joins = new ArrayList<>();
        }
        Joiner joiner = new Joiner(type, t, on);
        this.joins.add(joiner);
    }

    @Override
    public WhereClause where(Filter f) {
        this.where = f;
        return this;
    }

    @Override
    public SelectEndClause limit(int skip, int take) {
        this.skip = skip;
        this.take = take;
        return this;
    }

    @Override
    public SelectEndClause page(int index, int size) {
        this.skip = (index - 1) * size;
        this.take = size;
        return this;
    }

    @Override
    public SelectEndClause lock(LockMode mode) {
        this.lock = mode;
        return this;
    }

    @Override
    public HavingClause having(Filter f) {
        this.having = f;
        return this;
    }

    @Override
    public GroupByClause groupBy(Groupers groupers) {
        if (groupers != null) {
            this.groups = groupers.getItems();
        }
        return this;
    }

    @Override
    public OrderByClause orderBy(Sorters sorters) {
        if (sorters != null) {
            this.orders = sorters.getItems();
        }
        return this;
    }

    @Override
    public FromClause from(Table table) {
        this.table = table;
        return this;
    }

    @Override
    public FromClause from(String table) {
        this.table = Table.create(table);
        return this;
    }

    @Override
    public <T> T value(Class<T> type) {
        BuildResult result = builder.buildSelect(this);
        return operations.queryForObject(result.getSql(), result.getArgs(), type);
    }

    @Override
    public Map<String, Object> one() {
        BuildResult result = builder.buildSelect(this);
        List<Map<String, Object>> list = operations.query(result.getSql(), result.getArgs(), new RowMapperResultSetExtractor<>(new ColumnMapRowMapper()));
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
//        return operations.queryForMap(result.getSql(), result.getArgs().toArray());
    }

    @Override
    public <T> T one(Class<T> type) {
        List<T> list = list(type);
        return list.isEmpty() ? null : list.get(0);
//        BuildResult result = builder.buildSelect(this);
//        return operations.queryForObject(result.getSql(), result.getArgs().toArray(), mapperFactory.get(type));
//        return operations.queryForObject(result.getSql(), result.getArgs().toArray(), BeanPropertyRowMapper.newInstance(type));
    }

    @Override
    public <T> List<T> list(Class<T> type) {
        BuildResult result = builder.buildSelect(this);
        BeanListExtractor<T> extractor = new BeanListExtractor<>(mapperFactory.get(type));
        return operations.query(result.getSql(), result.getArgs(), extractor);
//        return operations.query(result.getSql(), result.getArgs().toArray(), BeanPropertyRowMapper.newInstance(type));
    }

    /**
     * Join type
     */
    public enum JoinType {
        /**
         * inner join
         */
        INNER("JOIN"),
        /**
         * left join
         */
        LEFT("LEFT JOIN"),
        /**
         * right join
         */
        RIGHT("RIGHT JOIN"),
        /**
         * full join
         */
        FULL("FULL JOIN");

        String value;

        JoinType(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }
    }

    public static class Joiner {
        JoinType type;
        Table table;
        Filter on;

        Joiner(JoinType type, Table table, Filter on) {
            this.type = type;
            this.table = table;
            this.on = on;
        }

        public JoinType getType() {
            return type;
        }

        public Table getTable() {
            return table;
        }

        public Filter getOn() {
            return on;
        }
    }
}
