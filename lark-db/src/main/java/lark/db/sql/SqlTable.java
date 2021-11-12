package lark.db.sql;

import java.lang.annotation.*;

/**
 * Created by noname on 15/11/19.
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlTable {
    /**
     * 表分片字段
     *
     * @return
     */
    String[] shardKeys() default {};
}
