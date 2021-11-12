package lark.msg;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author cuigh
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Component
public @interface MsgHandler {
    @AliasFor(
            annotation = Component.class
    )
    String value() default "";

    String topic() default "";

    String channel() default "";

    int threads() default 4;

    Class<? extends Subscriber> provider();
}
