package lark.core.util;

import lark.core.lang.EnumValuable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 枚举辅助类。
 *
 * @author cuigh
 */
public final class Enums {
    private final static Map<Class<?>, Object> CACHES = new ConcurrentHashMap<>();

    /**
     * 根据指定的枚举值获取枚举实例
     *
     * @param type  enum class
     * @param value enum value
     * @param <T>   enum type
     * @return
     */
    public static <T extends EnumValuable> T valueOf(Class<T> type, int value) {
        return valueOf(type, value, true);
    }

    /**
     * @param type
     * @param value
     * @param throwIfUndefined 指示枚举值未定义时是否抛出异常。
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends EnumValuable> T valueOf(Class<T> type, int value, boolean throwIfUndefined) {
        Map<Integer, T> map = getValues(type);
        T v = map.get(value);
        if (v == null && throwIfUndefined) {
            throw new RuntimeException(String.format("Undefined enum %s: %s", type.getSimpleName(), value));
        }
        return v;
    }

    /**
     * 判断是否为已定义的枚举值。
     *
     * @param type
     * @param value
     * @param <T>
     * @return
     */
    public static <T extends EnumValuable> boolean isDefined(Class<T> type, int value) {
        Map<Integer, T> map = getValues(type);
        return map.containsKey(value);
    }

    @SuppressWarnings("unchecked")
    private static <T extends EnumValuable> Map<Integer, T> getValues(Class<T> enumClass) {
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException(enumClass + " is not enum type");
        }

        return (Map<Integer, T>) CACHES.computeIfAbsent(enumClass, c -> {
            T[] values = enumClass.getEnumConstants();
            Map<Integer, T> map = new HashMap<>(values.length);
            for (T v : values) {
                map.put(v.value(), v);
            }
            return map;
        });
    }
}

