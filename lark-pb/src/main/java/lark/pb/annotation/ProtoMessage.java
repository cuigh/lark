package lark.pb.annotation;

import java.lang.annotation.*;

/**
 * @author cuigh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Documented
public @interface ProtoMessage {
    /**
     * @return description to the field
     */
    String description() default "";
}
