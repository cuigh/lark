package lark.db.sql;

import lombok.Getter;

/**
 * 过滤条件
 *
 * @author cuigh
 */
public abstract class Filter {
    /**
     * 返回一个对立的条件
     *
     * @return
     */
    public static Filter not(Filter filter) {
        return new NotFilter(filter);
    }

    /**
     * 返回一个与关系的条件
     *
     * @param filter1
     * @param filter2
     * @return
     */
    public static Filter and(Filter filter1, Filter filter2) {
        return new AndFilter(filter1, filter2);
    }

    /**
     * 返回一个或关系的条件
     *
     * @param filter1
     * @param filter2
     * @return
     */
    public static Filter or(Filter filter1, Filter filter2) {
        return new OrFilter(filter1, filter2);
    }

    /**
     * 创建一个基础过滤条件
     *
     * @return
     */
    public static Criteria create() {
        return new Criteria();
    }

    /**
     * 创建一个基础过滤条件
     *
     * @param column
     * @param value
     * @return
     */
    public static Criteria create(String column, Object value) {
        return new Criteria(column, value);
    }

    public static class AndFilter extends Filter {
        @Getter
        private Filter left;
        @Getter
        private Filter right;

        AndFilter(Filter left, Filter right) {
            this.left = left;
            this.right = right;
        }
    }

    public static class OrFilter extends Filter {
        @Getter
        private Filter left;
        @Getter
        private Filter right;

        OrFilter(Filter left, Filter right) {
            this.left = left;
            this.right = right;
        }
    }

    public static class NotFilter extends Filter {
        @Getter
        private Filter inner;

        NotFilter(Filter filter) {
            this.inner = filter;
        }
    }
}
