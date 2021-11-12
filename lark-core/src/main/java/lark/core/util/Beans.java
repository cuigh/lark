package lark.core.util;

import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * @author cuigh
 */
public final class Beans {
    public static <T> T map(Object source, Class<T> targetClass) {
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw Exceptions.asRuntime(e);
        }
    }

    public static String getGetterName(Field f) {
        String getter = f.getName();
        if (f.getType() != boolean.class) {
            getter = "get" + StringUtils.capitalize(getter);
        } else if (!startWithIs(getter)) {
            getter = "is" + StringUtils.capitalize(getter);
        }
        return getter;
    }

    public static String getSetterName(Field f) {
        String name = f.getName();
        if (f.getType() == boolean.class && startWithIs(name)) {
            name = name.substring(2);
        }
        return "set" + StringUtils.capitalize(name);
    }

    private static boolean startWithIs(String name) {
        return name.startsWith("is") && name.length() > 2 && Character.isUpperCase(name.charAt(2));
    }
}
