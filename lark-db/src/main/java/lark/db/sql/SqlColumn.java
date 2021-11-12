package lark.db.sql;

import java.lang.annotation.*;

/**
 * Created by noname on 15/11/19.
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlColumn {
    /**
     * 是否是 JSON 类型
     *
     * @return 如果是 JSON 类型，返回 true
     */
    boolean json() default false;
}
