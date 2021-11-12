package lark.core.security;

import java.lang.annotation.*;

/**
 * @author cuigh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Documented
public @interface Permission {
    String type();

    String id();
}
