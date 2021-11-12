package lark.net.rpc.annotation;

import lark.net.rpc.client.InvokeMode;

import java.lang.annotation.*;

/**
 * @author cuigh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Documented
public @interface RpcService {
    /**
     * Service name
     *
     * @return Service name
     */
    String name() default "";

    /**
     * Service description
     *
     * @return Service description
     */
    String description() default "";

    /**
     * Invoke mode
     *
     * @return Invoke mode
     */
    InvokeMode invoke() default InvokeMode.FAIL_OVER;
}
