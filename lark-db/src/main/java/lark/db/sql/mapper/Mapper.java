package lark.db.sql.mapper;

import com.esotericsoftware.reflectasm.MethodAccess;
import lark.core.codec.JsonCodec;
import lark.core.util.Beans;
import lark.core.util.Exceptions;
import lark.db.sql.NamingStyle;
import lark.db.sql.SqlColumn;
import lark.db.sql.SqlException;
import lark.db.sql.SqlTable;
import lark.db.sql.converter.ConverterManager;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实体映射辅助类
 *
 * @param <T> 实体类型
 * @author cuigh
 */
public class Mapper<T> implements RowMapper<T> {
    /**
     * 有效字段(有对应的 get/set 方法)
     */
    Map<String, FieldInfo> fields = new HashMap<>();
    private Class<T> type;
    /**
     * 实体对应的表名
     */
    private String table;
    /**
     * 删除,更新条件字段(带 Id 注解的)
     */
    private String[] idColumns;
    /**
     * 插入字段(不带 GeneratedValue 注解的)
     */
    private String[] insertColumns;
    /**
     * 更新字段(不带 Id 注解的)
     */
    private String[] updateColumns;
    /**
     * 表分片字段
     */
    private String[] shardKeys;
    private MethodAccess method;

    Mapper(Class<T> type, MapperOptions options) {
        this.type = type;
        this.method = MethodAccess.get(type);

        SqlTable sqlTable = type.getAnnotation(SqlTable.class);
        if (sqlTable != null) {
            this.shardKeys = sqlTable.shardKeys();
        }

        Table tableAnnotation = type.getAnnotation(Table.class);
        if (tableAnnotation == null || StringUtils.isEmpty(tableAnnotation.name())) {
            this.table = NamingStyle.transform(type.getSimpleName(), options.tableNaming);
        } else {
            this.table = tableAnnotation.name();
        }

        initField(type, options.columnNaming);
    }

    private static String getFieldName(Field f, String pascalName, NamingStyle nameStyle) {
        String name;

        javax.persistence.Column columnAnno = f.getAnnotation(javax.persistence.Column.class);
        if (columnAnno == null || StringUtils.isEmpty(columnAnno.name())) {
            name = NamingStyle.transform(pascalName, nameStyle);
        } else {
            name = columnAnno.name();
        }

        return name;
    }

    public Class<T> getType() {
        return type;
    }

    public String getTable() {
        return table;
    }

    private void initField(Class<?> clazz, NamingStyle nameStyle) {
        Field[] declaredFields = clazz.getDeclaredFields();
        List<String> tempIdColumns = new ArrayList<>();
        for (Field f : declaredFields) {
            FieldInfo fi = getFieldInfo(f);
            if (fi == null) {
                continue;
            }

            String name = getFieldName(f, f.getName(), nameStyle);
            Id idAnno = f.getAnnotation(Id.class);
            if (idAnno != null) {
                fi.key = true;
                tempIdColumns.add(name);
            }

            GeneratedValue gvAnno = f.getAnnotation(GeneratedValue.class);
            if (gvAnno != null) {
                fi.auto = true;
            }

            SqlColumn anno = f.getAnnotation(SqlColumn.class);
            if (anno != null) {
                fi.json = anno.json();
            }

            this.fields.put(name, fi);
        }

        if (!tempIdColumns.isEmpty()) {
            this.idColumns = tempIdColumns.toArray(new String[0]);
        }

        // 如果有继承关系，需要增加对父类的处理，这里是属于嵌套
        if (clazz.getSuperclass() != Object.class) {
            initField(clazz.getSuperclass(), nameStyle);
        }
    }

    private FieldInfo getFieldInfo(Field f) {
        if (f.isAnnotationPresent(Transient.class)) {
            return null;
        }

        int getIndex = this.getMethodIndex(Beans.getGetterName(f));
        if (getIndex == -1) {
            return null;
        }

        int setIndex = this.getMethodIndex(Beans.getSetterName(f));
        if (setIndex == -1) {
            return null;
        }

        return new FieldInfo(getIndex, setIndex, f);
    }

    public String[] getShardKeys() {
        return this.shardKeys;
    }

    public Object[] getShardValues(Object obj) {
        if (this.shardKeys.length == 0) {
            throw new SqlException("shard keys aren't specified for class: " + obj.getClass().getName());
        }

        return getValues(obj, shardKeys);
    }

    public String[] getInsertColumns() {
        if (this.insertColumns == null) {
            List<String> columns = new ArrayList<>(fields.size());
            fields.forEach((key, fi) -> {
                if (!fi.auto) {
                    columns.add(key);
                }
            });
            this.insertColumns = columns.toArray(new String[0]);
        }
        return this.insertColumns;
    }

    public Object[] getInsertValues(Object obj) {
        String[] columns = this.getInsertColumns();
        return getValues(obj, columns);
    }

    public String[] getUpdateColumns() {
        if (this.updateColumns == null) {
            List<String> columns = new ArrayList<>(fields.size());
            fields.forEach((key, fi) -> {
                if (!fi.key) {
                    columns.add(key);
                }
            });
            this.updateColumns = columns.toArray(new String[0]);
        }
        return this.updateColumns;
    }

    public Object[] getUpdateValues(Object obj) {
        String[] columns = this.getUpdateColumns();
        return getValues(obj, columns);
    }

    public String[] getIdColumns() {
        return this.idColumns;
    }

    public Object[] getIdValues(Object obj) {
        String[] columns = this.getIdColumns();
        return getValues(obj, columns);
    }

    public Object getValue(Object obj, String field) {
        FieldInfo fi = fields.get(field);
        if (fi == null) {
            return null;
        }

        if (fi.json) {
            return JsonCodec.encode(method.invoke(obj, fi.getIndex));
        } else {
            return ConverterManager.j2d(method.invoke(obj, fi.getIndex));
        }
    }

    private Object[] getValues(Object obj, String[] columns) {
        Object[] values = new Object[columns.length];
        for (int i = 0; i < columns.length; i++) {
            values[i] = getValue(obj, columns[i]);
        }
        return values;
    }

    /**
     * 设置对象的值
     *
     * @param obj   实体对象
     * @param field 字段名
     * @param value 值
     */
    public void setValue(Object obj, String field, Object value) {
        if (value == null) {
            return;
        }

        FieldInfo fi = fields.get(field);
        if (fi == null) {
            return;
        }

        Object v = fi.json ? JsonCodec.decode(value.toString(), fi.type) : ConverterManager.d2j(fi.clazz, value);
        method.invoke(obj, fi.setIndex, v);
    }

    private int getMethodIndex(String name) {
        String[] names = method.getMethodNames();
        for (int i = 0; i < names.length; ++i) {
            if (name.equals(names[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) {
        try {
            T entity = type.getDeclaredConstructor().newInstance();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                Object value = rs.getObject(i);
                if (value != null) {
                    setValue(entity, metaData.getColumnLabel(i), value);
                }
            }
            return entity;
        } catch (Exception e) {
            throw Exceptions.asRuntime(e);
        }
    }

    static class FieldInfo {
        int getIndex;
        int setIndex;
        boolean key;
        boolean auto;
        Class<?> clazz;
        boolean json;
        Type type;

        FieldInfo(int getIndex, int setIndex, Field field) {
            this.getIndex = getIndex;
            this.setIndex = setIndex;
            this.clazz = field.getType();
            this.type = field.getGenericType();
        }
    }
}

