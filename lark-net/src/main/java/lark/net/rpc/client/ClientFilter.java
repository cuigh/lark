package lark.net.rpc.client;

/**
 * @author cuigh
 */
public interface ClientFilter {
    CallHandler apply(CallHandler handler);
}
