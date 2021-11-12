package lark.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常辅助类。
 *
 * @author cuigh
 */
public class Exceptions {
    private Exceptions() {
        // 禁止实例化
    }

    /**
     * Convert a throwable to RuntimeException
     *
     * @param e Original exception
     * @return unchecked RuntimeException
     */
    public static RuntimeException asRuntime(Throwable e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException(e);
    }

    /**
     * Returns a string containing the result of {@link Throwable#toString() toString()}, followed by
     * the full, recursive stack trace of {@code throwable}. Note that you probably should not be
     * parsing the resulting string; if you need programmatic access to the stack frames, you can call
     * {@link Throwable#getStackTrace()}.
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
