package lark.net.rpc.client.invoker;

import lark.net.rpc.client.InvokeMode;

/**
 * @author cuigh
 */
public class InvokerFactory {
    public static Invoker create(InvokeMode mode) {
        switch (mode) {
            case FAIL_FAST:
                return new FailFastInvoker();
            default:
                return new FailOverInvoker();
        }
    }
}
