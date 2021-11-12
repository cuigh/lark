package lark.core.context;

import java.util.Objects;

/**
 * @author cuigh
 */
public class Context {
    public static final Context TODO = new Context();
    private static final ThreadLocal<Context> holder = ThreadLocal.withInitial(() -> TODO);

    protected Context() {
    }

    public static Context get() {
        return holder.get();
    }

    public static void set(Context ctx) {
        holder.set(ctx);
    }

    public static Context withValue(Context ctx, Object key, Object value) {
        return new ValueContext(ctx, key, value);
    }

    public static Context withDeadline(Context ctx, long deadline) {
        long d = ctx.getDeadline();
        if (d == 0 || deadline < d) {
            return new DeadlineContext(ctx, deadline);
        }
        return ctx;
    }

    public static Context withTimeout(Context ctx, long timeout) {
        return withDeadline(ctx, System.currentTimeMillis() + timeout);
    }

    /**
     * 返回上下文超时时间(毫秒)
     *
     * @return 截止时间，0 表示无截至时间
     */
    public final long getTimeout() {
        long timeout = this.getDeadline() - System.currentTimeMillis();
        return timeout > 0 ? timeout : 0;
    }

    public static void setTimeout(long timeout) {
        holder.set(withTimeout(holder.get(), timeout));
    }

    /**
     * 返回上下文截至时间
     *
     * @return 截止时间，0 表示无截至时间
     * @see System#currentTimeMillis()
     */
    public long getDeadline() {
        return 0;
    }

    public static void setDeadline(long deadline) {
        holder.set(withDeadline(holder.get(), deadline));
    }

    /***
     * 返回关联的属性值
     *
     * @param key 键
     * @return 值
     */
    public Object getValue(Object key) {
        return null;
    }

    private static class ValueContext extends Context {
        private Context parent;
        private Object key;
        private Object value;

        ValueContext(Context parent, Object key, Object value) {
            Objects.requireNonNull(key);
            this.parent = parent;
            this.key = key;
            this.value = value;
        }

        @Override
        public long getDeadline() {
            return parent.getDeadline();
        }

        @Override
        public Object getValue(Object key) {
            if (this.key.equals(key)) {
                return this.value;
            }
            return parent.getValue(key);
        }
    }

    private static class DeadlineContext extends Context {
        private Context parent;
        private long deadline;

        DeadlineContext(Context parent, long deadline) {
            this.parent = parent;
            this.deadline = deadline;
        }

        @Override
        public long getDeadline() {
            return deadline;
        }

        @Override
        public Object getValue(Object key) {
            return parent.getValue(key);
        }
    }
}
