package lark.core.util;

import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cuigh
 */
public final class Strings {
    public static boolean isEmpty(String s) {
        return StringUtils.isEmpty(s);
    }

    public static String[] split(String s, String sep) {
        if (StringUtils.isEmpty(s)) {
            return new String[0];
        }

        return s.split(sep);
    }

    public static Map<String, String> split(String s, String sep1, String sep2) {
        if (StringUtils.isEmpty(s)) {
            return Collections.emptyMap();
        }

        String[] parts = s.split(sep1);
        Map<String, String> m = new HashMap<>(parts.length);
        for (String part : parts) {
            String[] pair = part.split(sep2);
            m.put(pair[0], pair[1]);
        }
        return m;
    }
}
