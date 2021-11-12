package lark.net.rpc.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author cuigh
 */
@FunctionalInterface
public interface Matcher {
//    Matcher ALWAYS = (ctx, in) -> true;

    boolean match(ChannelHandlerContext ctx, ByteBuf in);
}
