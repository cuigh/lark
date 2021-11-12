package lark.core.util;

/**
 * @author cuigh
 */
public final class Arrays {
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }
}
