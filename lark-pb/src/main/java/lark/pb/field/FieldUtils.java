package lark.pb.field;

import lark.pb.ProtoException;
import lark.pb.annotation.ProtoField;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Field utility class
 *
 * @author cuigh
 */
@SuppressWarnings("unchecked")
public final class FieldUtils {
    private static Map<Class<?>, FieldType> typeMapping = new HashMap<>();

    static {
        typeMapping.put(int.class, FieldType.INT32);
        typeMapping.put(Integer.class, FieldType.INT32);
        typeMapping.put(short.class, FieldType.INT32);
        typeMapping.put(Short.class, FieldType.INT32);
        typeMapping.put(Byte.class, FieldType.INT32);
        typeMapping.put(byte.class, FieldType.INT32);
        typeMapping.put(long.class, FieldType.INT64);
        typeMapping.put(Long.class, FieldType.INT64);
        typeMapping.put(Date.class, FieldType.INT64);
        typeMapping.put(LocalDate.class, FieldType.INT64);
        typeMapping.put(LocalTime.class, FieldType.INT64);
        typeMapping.put(LocalDateTime.class, FieldType.INT64);
        typeMapping.put(ZonedDateTime.class, FieldType.INT64);
        typeMapping.put(String.class, FieldType.STRING);
        typeMapping.put(BigDecimal.class, FieldType.STRING);
        typeMapping.put(byte[].class, FieldType.BYTES);
        typeMapping.put(Byte[].class, FieldType.BYTES);
        typeMapping.put(Float.class, FieldType.FLOAT);
        typeMapping.put(float.class, FieldType.FLOAT);
        typeMapping.put(double.class, FieldType.DOUBLE);
        typeMapping.put(Double.class, FieldType.DOUBLE);
        typeMapping.put(Boolean.class, FieldType.BOOL);
        typeMapping.put(boolean.class, FieldType.BOOL);
    }

    public static FieldType getFieldType(Class<?> type) {
        FieldType fieldType = typeMapping.get(type);
        if (fieldType == null) {
            if (Enum.class.isAssignableFrom(type)) {
                fieldType = FieldType.ENUM;
            } else {
                fieldType = FieldType.MESSAGE;
            }
        }
        return fieldType;
    }

    /**
     * Find all fields marked with {@link ProtoField} annotation
     *
     * @param clazz target class
     * @return matched {@link Field} list
     */
    public static List<FieldInfo> getProtoFields(Class clazz) {
        List<FieldInfo> list = new ArrayList<>();
        Class cls = clazz;
        do {
            Field[] fields = cls.getDeclaredFields();
            for (Field f : fields) {
                if (f.isAnnotationPresent(ProtoField.class)) {
                    list.add(new FieldInfo(clazz, f));
                }
            }
            cls = cls.getSuperclass();
        } while (cls != null && cls != Object.class);

        validateFields(list);
        return list;
    }

    private static void validateFields(List<FieldInfo> fields) {
        Map<Integer, FieldInfo> orders = new HashMap<>(fields.size());
        fields.forEach(f -> {
            FieldInfo info = orders.putIfAbsent(f.getOrder(), f);
            if (info != null) {
                String msg = String.format("Duplicate order '%d' for field '%s' and '%s'",
                        f.getOrder(), info.getField().getName(), f.getField().getName());
                throw new ProtoException(msg);
            }
        });
    }
}
