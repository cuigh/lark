package lark.net.rpc.server;

/**
 * @author cuigh
 */
public interface InvokeHandler {
    /**
     * Handle request
     *
     * @param ctx request context
     * @return invoke result
     */
    Object handle(InvokeContext ctx);
}
