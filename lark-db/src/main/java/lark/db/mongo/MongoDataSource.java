package lark.db.mongo;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MongoDataSource {

    /**
     * 数据源名称
     * @return
     */
    String name();
}
