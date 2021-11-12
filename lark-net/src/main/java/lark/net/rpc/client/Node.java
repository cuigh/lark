package lark.net.rpc.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.pool.ChannelPool;
import io.netty.util.concurrent.Future;
import lark.core.lang.BusinessException;
import lark.net.Address;
import lark.net.rpc.ResponseMessage;
import lark.net.rpc.RpcError;
import lark.net.rpc.RpcException;

import java.util.List;

/**
 * @author cuigh
 */
public class Node implements CallHandler {
    private final CallPool callPool = new CallPool();
    private final String server;
    private final Address address;
    private final ClientOptions options;
    private List<ClientFilter> filters;
    private ChannelPool pool;
    private Node next;
    private CallHandler handler = this;

    public Node(String server, Address address, ClientOptions options, List<ClientFilter> filters) {
        this.server = server;
        this.address = address;
        this.options = options;
        this.filters = filters;
        this.pool = ClientChannelPool.get(address, options);
        if (filters != null) {
            for (ClientFilter filter : this.filters) {
                this.handler = filter.apply(this.handler);
            }
        }
    }

    public Node getNext() {
        return next;
    }

    void setNext(Node next) {
        this.next = next;
    }

    public Object invoke(String service, String method, Object[] args, Class<?> returnType) {
        Call call = callPool.acquire();
        call.reset(server, service, method, args, returnType);

        try {
            Future<Channel> future = pool.acquire().awaitUninterruptibly();
            if (!future.isSuccess()) {
                throw new RpcException(RpcError.CLIENT_ACQUIRE_FAILED, future.cause());
            }
            call.channel = (ClientChannel) future.getNow();
            return this.handler.handle(call);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new RpcException(RpcError.CLIENT_UNKNOWN_ERROR, e);
        } finally {
            callPool.release(call);
        }
    }

    private void sendRequest(Call call) {
        try {
            ChannelFuture future = call.channel.send(call);
            if (!future.isSuccess()) {
                throw new RpcException(RpcError.CLIENT_WRITE_FAILED, future.cause());
            }
        } finally {
            pool.release(call.channel);
        }
    }

    private Object waitResponse(Call call) {
        ResponseMessage message;
        try {
            message = call.get(this.options.getReadTimeout());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new RpcException(RpcError.CLIENT_UNKNOWN_ERROR, e);
        }

        if (message == null) {
            String error = String.format("获取服务器响应超时(%dms)", this.options.getReadTimeout().toMillis());
            throw new RpcException(RpcError.CLIENT_READ_FAILED.value(), error);
        }

        return message.decode(call.returnType);
    }

    @Override
    public Object handle(Call call) {
        sendRequest(call);
        return waitResponse(call);
    }
}
