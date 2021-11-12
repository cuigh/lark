package lark.db.sql;

import lark.core.sync.Pool;
import lark.core.util.Arrays;
import lark.db.sql.builder.Builder;
import lark.db.sql.clause.*;
import lark.db.sql.context.*;
import lark.db.sql.mapper.Mapper;
import lark.db.sql.mapper.MapperFactory;
import lark.db.sql.mapper.MapperOptions;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;

/**
 * @author cuigh
 */
public class SqlQuery {
    private static final String PARAMETER_NULL = "parameter 'object' can't be null";
    private final MapperFactory mapperFactory;
    private final Pool<InsertContext> insertPool;
    private final Pool<DeleteContext> deletePool;
    private final Pool<UpdateContext> updatePool;
    private final Pool<SelectContext> selectPool;
    private final Pool<ExecuteContext> executePool;

    /**
     * Construct a new SqlQuery
     *
     * @param source the JDBC DataSource to obtain connections from
     */
    public SqlQuery(DataSource source) {
        this(source, DatabaseType.fromMetaData(source), new MapperOptions());
    }

    /**
     * Construct a new SqlQuery
     *
     * @param source the JDBC DataSource to obtain connections from
     * @param type   database type
     */
    public SqlQuery(DataSource source, DatabaseType type) {
        this(source, type, new MapperOptions());
    }

    /**
     * Construct a new SqlQuery
     *
     * @param source        the JDBC DataSource to obtain connections from
     * @param type          database type
     * @param mapperOptions the options used for Mapper
     */
    public SqlQuery(DataSource source, DatabaseType type, MapperOptions mapperOptions) {
        Objects.requireNonNull(source);

        JdbcOperations operations = new JdbcTemplate(source);
        Builder builder = Builder.create(type);
        this.mapperFactory = new MapperFactory(mapperOptions);
        this.insertPool = new Pool<>(() -> new InsertContext(operations, builder, mapperFactory));
        this.deletePool = new Pool<>(() -> new DeleteContext(operations, builder, mapperFactory));
        this.updatePool = new Pool<>(() -> new UpdateContext(operations, builder, mapperFactory));
        this.selectPool = new Pool<>(() -> new SelectContext(operations, builder, mapperFactory));
        this.executePool = new Pool<>(() -> new ExecuteContext(operations, builder, mapperFactory));
    }

    /**
     * Insert operation
     *
     * @param table table name
     * @return InsertClause
     */
    public InsertClause insert(String table) {
        InsertContext ctx = insertPool.acquire();
        ctx.reset(table);
        return ctx;
    }


    /**
     * Insert operation
     *
     * @param object entity object
     * @return insert result
     */
    public InsertResult insert(Object object) {
        Objects.requireNonNull(object, PARAMETER_NULL);

        Mapper mapper = mapperFactory.get(object.getClass());
        InsertContext ctx = insertPool.acquire();
        ctx.reset(mapper.getTable());
        try {
            return ctx.columns(mapper.getInsertColumns()).values(mapper.getInsertValues(object)).result();
        } finally {
            insertPool.release(ctx);
        }
    }

    /**
     * Insert operation
     *
     * @param objects object list
     * @param <T>     object type
     * @return insert result
     */
    public <T> InsertResult insert(List<T> objects) {
        if (CollectionUtils.isEmpty(objects)) {
            throw new SqlException("parameter 'objects' can't be null or empty");
        }

        Mapper mapper = mapperFactory.get(objects.get(0).getClass());
        InsertContext ctx = insertPool.acquire();
        ctx.reset(mapper.getTable());
        ctx.columns(mapper.getInsertColumns());
        objects.forEach(object -> ctx.values(mapper.getInsertValues(object)));
        try {
            return ctx.result();
        } finally {
            insertPool.release(ctx);
        }
    }

    /**
     * Insert operation
     *
     * @param object entity object
     * @return affect rows
     */
    public int create(Object object) {
        Objects.requireNonNull(object, PARAMETER_NULL);

        Mapper mapper = mapperFactory.get(object.getClass());
        InsertContext ctx = insertPool.acquire();
        ctx.reset(mapper.getTable());
        ctx.columns(mapper.getInsertColumns()).values(mapper.getInsertValues(object));
        try {
            return ctx.submit();
        } finally {
            insertPool.release(ctx);
        }
    }

    /**
     * Insert operation
     *
     * @param objects object list
     * @param <T>     object type
     * @return affect rows
     */
    public <T> int create(List<T> objects) {
        if (CollectionUtils.isEmpty(objects)) {
            throw new SqlException("parameter 'objects' can't be null or empty");
        }

        Mapper mapper = mapperFactory.get(objects.get(0).getClass());
        InsertContext ctx = insertPool.acquire();
        ctx.reset(mapper.getTable());
        ctx.columns(mapper.getInsertColumns());
        objects.forEach(object -> ctx.values(mapper.getInsertValues(object)));
        try {
            return ctx.submit();
        } finally {
            insertPool.release(ctx);
        }
    }

    /**
     * Delete operation
     *
     * @param table  table name
     * @param filter where condition
     * @return affect rows
     */
    public int delete(String table, Filter filter) {
        DeleteContext ctx = deletePool.acquire();
        ctx.reset(table);
        try {
            return ctx.where(filter).submit();
        } finally {
            deletePool.release(ctx);
        }
    }

    /**
     * Delete operation
     *
     * @param object object for deleting
     * @return affect rows
     */
    public int delete(Object object) {
        Objects.requireNonNull(object, PARAMETER_NULL);

        Mapper mapper = mapperFactory.get(object.getClass());
        String[] idColumns = ensureIdColumns(mapper);

        Criteria filter = Filter.create();
        for (String col : idColumns) {
            filter.eq(col, mapper.getValue(object, col));
        }

        DeleteContext ctx = deletePool.acquire();
        ctx.reset(mapper.getTable());
        try {
            return ctx.where(filter).submit();
        } finally {
            deletePool.release(ctx);
        }
    }

    /**
     * Delete operation
     *
     * @param type object type
     * @param id   object id
     * @return affect rows
     */
    public int delete(Class<?> type, Object id) {
        Mapper mapper = mapperFactory.get(type);
        String[] idColumns = mapper.getIdColumns();
        if (Arrays.isEmpty(idColumns)) {
            String error = String.format("实体类型 %s 必须且只能有一个带 Id 注解的属性", type.getName());
            throw new SqlException(error);
        }

        DeleteContext ctx = deletePool.acquire();
        ctx.reset(mapper.getTable());
        try {
            return ctx.where(Filter.create(idColumns[0], id)).submit();
        } finally {
            deletePool.release(ctx);
        }
    }

    /**
     * Update operation
     *
     * @param table table name
     * @return UpdateClause
     */
    public UpdateClause update(String table) {
        UpdateContext ctx = updatePool.acquire();
        ctx.reset(table);
        return ctx;
    }

    /**
     * Update operation
     *
     * @param object  entity object
     * @param columns the columns for updating
     * @return affect rows
     */
    public int update(Object object, String... columns) {
        Objects.requireNonNull(object, PARAMETER_NULL);

        Mapper mapper = mapperFactory.get(object.getClass());
        String[] idColumns = mapper.getIdColumns();
        if (idColumns == null) {
            throw new SqlException(String.format("实体类型 %s 没有定义 ID 列", object.getClass().getName()));
        }

        Updaters values = new Updaters();
        String[] updateColumns = Arrays.isEmpty(columns) ? mapper.getUpdateColumns() : columns;
        for (String col : updateColumns) {
            values.eq(col, mapper.getValue(object, col));
        }

        Criteria filter = Filter.create();
        for (String col : idColumns) {
            filter.eq(col, mapper.getValue(object, col));
        }

        UpdateContext ctx = updatePool.acquire();
        ctx.reset(mapper.getTable());
        try {
            return ctx.set(values).where(filter).submit();
        } finally {
            updatePool.release(ctx);
        }
    }

    /**
     * Select operation
     *
     * @param columns the columns for selecting
     * @return SelectClause
     */
    public SelectClause select(String... columns) {
        return select(new Columns(null, columns));
    }

    /**
     * Select operation
     *
     * @param columns the columns for selecting
     * @return SelectClause
     */
    public SelectClause select(Columns columns) {
        SelectContext ctx = selectPool.acquire();
        ctx.reset(columns);
        return ctx;
    }

    /**
     * Select operation
     *
     * @param distinct if true, remove duplicated rows
     * @param columns  the columns for selecting
     * @return SelectClause
     */
    public SelectClause select(boolean distinct, Columns columns) {
        SelectContext ctx = selectPool.acquire();
        ctx.reset(columns);
        ctx.setDistinct(distinct);
        return ctx;
    }

    /**
     * Select operation
     *
     * @param type the entity type
     * @return FromClause
     */
    public FromClause select(Class<?> type) {
        Mapper mapper = mapperFactory.get(type);
        SelectContext ctx = selectPool.acquire();
        ctx.reset(new Columns(null, mapper.getUpdateColumns()));
        return ctx;
    }

    /**
     * Select operation
     *
     * @param object the entity for generating criteria
     * @return SelectEndClause
     */
    public SelectEndClause select(Object object) {
        Objects.requireNonNull(object, PARAMETER_NULL);

        Mapper mapper = mapperFactory.get(object.getClass());
        String[] idColumns = ensureIdColumns(mapper);

        Criteria filter = Filter.create();
        Columns columns = new Columns(null, mapper.getUpdateColumns());
        columns.add(null, idColumns);
        for (String c : idColumns) {
            filter.eq(c, mapper.getValue(object, c));
        }

        SelectContext ctx = selectPool.acquire();
        ctx.reset(columns);
        return ctx.from(mapper.getTable()).where(filter);
    }

    /**
     * Select operation
     *
     * @param object the entity for generating criteria
     * @return the result entity
     */
    @SuppressWarnings("unchecked")
    public <T> T find(T object) {
        Objects.requireNonNull(object, PARAMETER_NULL);

        Class<T> type = (Class<T>) object.getClass();
        Mapper mapper = mapperFactory.get(type);
        String[] idColumns = ensureIdColumns(mapper);

        Criteria filter = Filter.create();
        Columns columns = new Columns(null, mapper.getUpdateColumns());
        columns.add(null, idColumns);
        for (String c : idColumns) {
            filter.eq(c, mapper.getValue(object, c));
        }

        SelectContext ctx = selectPool.acquire();
        ctx.reset(columns);
        try {
            return ctx.from(mapper.getTable()).where(filter).one(type);
        } finally {
            selectPool.release(ctx);
        }
    }

    /**
     * Select operation
     *
     * @param type entity class
     * @param id   record id
     * @param <T>  entity type
     * @return result entity
     */
    public <T> T find(Class<T> type, Object id) {
        Mapper mapper = mapperFactory.get(type);
        String[] idColumns = mapper.getIdColumns();
        if (idColumns == null || idColumns.length != 1) {
            String error = String.format("实体类型 %s 必须且只能有一个带 Id 注解的属性", type.getName());
            throw new SqlException(error);
        }

        SelectContext ctx = selectPool.acquire();
        ctx.reset(new Columns(null, mapper.getUpdateColumns()).add(null, idColumns));
        try {
            return ctx.from(mapper.getTable()).where(Filter.create(idColumns[0], id)).one(type);
        } finally {
            selectPool.release(ctx);
        }
    }

    /**
     * Execute SQL directly
     *
     * @param sql  SQL clause
     * @param args parameters
     * @return ExecuteEndClause
     */
    public ExecuteEndClause execute(String sql, Object... args) {
        ExecuteContext ctx = executePool.acquire();
        ctx.reset(sql, args);
        return ctx;
    }

    private String[] ensureIdColumns(Mapper mapper) {
        String[] idColumns = mapper.getIdColumns();
        if (Arrays.isEmpty(idColumns)) {
            String error = String.format("entity type '%s' doesn't have any fields with @Id annotation", mapper.getType().getName());
            throw new SqlException(error);
        }
        return idColumns;
    }
}
