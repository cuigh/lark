package lark.task;

import java.lang.annotation.*;

/**
 * Created by noname on 15/11/27.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Task {
    String name() default "";

}
