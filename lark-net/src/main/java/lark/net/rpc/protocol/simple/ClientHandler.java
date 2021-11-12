package lark.net.rpc.protocol.simple;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lark.core.lang.BusinessException;
import lark.net.rpc.ResponseMessage;
import lark.net.rpc.client.ClientChannel;
import lark.net.rpc.protocol.simple.data.SimpleResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cuigh
 */
public class ClientHandler extends SimpleChannelInboundHandler<SimpleResponseMessage> {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SimpleResponseMessage msg) throws Exception {
        ResponseMessage response;
        if (msg.isSuccess()) {
            response = new SimpleResponseResult(msg);
        } else {
            BusinessException error = new BusinessException(msg.getErrorCode(), msg.getErrorInfo(), msg.getErrorDetail());
            response = ResponseMessage.failure(Long.parseLong(msg.getId()), error);
        }
        ClientChannel channel = (ClientChannel) ctx.channel();
        channel.receive(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // todo: 针对不同的异常类型做处理?
        ctx.close();
        logger.error("unknown client error", cause);
    }

    static class SimpleResponseResult implements ResponseMessage {
        private SimpleResponseMessage response;

        SimpleResponseResult(SimpleResponseMessage response) {
            this.response = response;
        }

        @Override
        public long getId() {
            return Long.parseLong(response.getId());
        }

        @Override
        public Object decode(Class<?> type) {
            return SimpleEncoder.decode(response.getResult().getDataType(), response.getResult().getData(), type);
        }
    }
}