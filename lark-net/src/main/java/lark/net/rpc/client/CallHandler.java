package lark.net.rpc.client;

/**
 * @author cuigh
 */
public interface CallHandler {
    Object handle(Call call);
}
