package lark.net.netty.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lark.pb.Codec;
import lark.pb.CodecFactory;

import java.util.List;

/**
 * 针对 lark-pb 包的 Google Protocol Buffers 编码器
 *
 * @author cuigh
 * @see io.netty.handler.codec.protobuf.ProtobufEncoder
 */
public class ProtobufEncoder extends MessageToMessageEncoder {
    private final Codec codec;

    public ProtobufEncoder(Class<?> cls) {
        this.codec = CodecFactory.get(cls);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void encode(ChannelHandlerContext ctx, Object msg, List out) throws Exception {
        out.add(codec.encode(msg));
    }
}