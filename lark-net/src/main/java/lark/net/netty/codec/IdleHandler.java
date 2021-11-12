package lark.net.netty.codec;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cuigh
 */
@Sharable
public class IdleHandler extends ChannelDuplexHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdleHandler.class);
    private final IdleState state;

    public IdleHandler(IdleState state) {
        this.state = state;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == state) {
                LOGGER.debug("channel {} is closed because of {} timeout.", ctx.channel(), state);
//                ctx.fireExceptionCaught(ReadTimeoutException.INSTANCE);
                ctx.close();
            }
        }
    }
}
