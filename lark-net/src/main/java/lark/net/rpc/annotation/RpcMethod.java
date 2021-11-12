package lark.net.rpc.annotation;

import lark.net.rpc.client.InvokeMode;

import java.lang.annotation.*;

/**
 * @author cuigh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
@Documented
public @interface RpcMethod {
    /**
     * Method name
     *
     * @return name
     */
    String name() default "";

    /**
     * Method description
     *
     * @return description
     */
    String description() default "";

    /**
     * Invoke mode
     *
     * @return Invoke mode
     */
    InvokeMode invoke() default InvokeMode.FAIL_OVER;
}
