package lark.net.rpc.protocol.proto;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lark.net.rpc.RequestMessage;
import lark.net.rpc.RpcError;
import lark.net.rpc.RpcException;
import lark.net.rpc.protocol.proto.data.Request;
import lark.net.rpc.server.*;
import lark.pb.Codec;
import lark.pb.CodecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务器端消息处理器
 *
 * @author cuigh
 */
public class ServerHandler extends SimpleChannelInboundHandler<Request> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);
    private final Server server;

    /**
     * 构造函数
     *
     * @param server RPC 服务端实例
     */
    public ServerHandler(Server server) {
        this.server = server;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request requestMessage) throws Exception {
        // 更新状态信息
        ChannelState state = ctx.channel().attr(ChannelState.KEY).get();
        if (state != null) {
            state.refresh(requestMessage.getClient(), requestMessage.getService(), requestMessage.getMethod());
        }

        ServiceContainer sc = server.getContainer();
        ServiceMethod method = sc.getMethod(requestMessage.getService(), requestMessage.getMethod());
        if (method == null) {
            throw new RpcException(RpcError.SERVER_SERVICE_NOT_FOUND, requestMessage.getService(), requestMessage.getMethod());
        }

        RequestMessage msg;
        try {
            msg = convert(method, requestMessage);
        } catch (Exception e) {
            InvokeResult result = InvokeResult.failure(requestMessage.getId(), e);
            ctx.writeAndFlush(result);
            return;
        }

        server.getDispatcher().offer(ctx.channel(), method, msg);
    }

    @SuppressWarnings("unchecked")
    private RequestMessage convert(ServiceMethod method, Request msg) throws IOException {
        RequestMessage m = new RequestMessage();
        m.setId(msg.getId());
        m.setService(msg.getService());
        m.setMethod(msg.getMethod());
        if (!CollectionUtils.isEmpty(msg.getLabels())) {
            Map<String, String> labels = new HashMap<>(msg.getLabels().size());
            msg.getLabels().forEach(l -> labels.put(l.getName(), l.getValue()));
            m.setLabels(labels);
        }
        if (!CollectionUtils.isEmpty(msg.getArgs())) {
            Object[] args = new Object[msg.getArgs().size()];
            for (int i = 0; i < args.length; i++) {
                Codec codec = CodecFactory.get(method.getParameterTypes()[i]);
                args[i] = codec.decode(msg.getArgs().get(i));
            }
            m.setArgs(args);
        }
        return m;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        // 针对不同的异常类型做处理?
        LOGGER.error("unknown server error", cause);
    }
}
