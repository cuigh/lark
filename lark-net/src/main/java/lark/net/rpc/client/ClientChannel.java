package lark.net.rpc.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioSocketChannel;
import lark.net.rpc.ResponseMessage;
import lark.net.rpc.RpcError;
import lark.net.rpc.RpcException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cuigh
 */
public class ClientChannel extends NioSocketChannel {
    private final Map<Long, Call> pendings = new ConcurrentHashMap<>();

    public void receive(ResponseMessage message) {
        Call call = pendings.remove(message.getId());
        if (call != null) {
            call.finish(message);
        }
    }

    ChannelFuture send(Call call) {
        pendings.put(call.getRequest().getId(), call);
        return writeAndFlush(call.getRequest()).awaitUninterruptibly();
    }

    @Override
    public ChannelFuture close() {
        ChannelFuture future = super.close();
        ResponseMessage message = ResponseMessage.failure(0, new RpcException(RpcError.CLIENT_CHANNEL_CLOSED));
        pendings.forEach((id, call) -> call.finish(message));
        pendings.clear();
        return future;
    }
}
