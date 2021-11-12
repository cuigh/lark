package lark.net.rpc.server;

/**
 * @author cuigh
 */
public interface ServerFilter {
    InvokeHandler apply(InvokeHandler handler);
}
