package lark.net.rpc.protocol.proto;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lark.net.rpc.client.ClientChannel;
import lark.net.rpc.protocol.proto.data.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cuigh
 */
public class ClientHandler extends SimpleChannelInboundHandler<Response> {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response msg) throws Exception {
        ClientChannel channel = (ClientChannel) ctx.channel();
        channel.receive(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // todo: 针对不同的异常类型做处理?
        ctx.close();
        logger.error("unknown client error", cause);
    }
}