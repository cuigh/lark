package lark.net.rpc.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author cuigh
 */
public interface ServerProtocol {
    boolean match(ChannelHandlerContext ctx, ByteBuf in);

    void initChannnel(ChannelHandlerContext ctx);
}
