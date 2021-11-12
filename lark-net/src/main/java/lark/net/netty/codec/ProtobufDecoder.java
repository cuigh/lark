package lark.net.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lark.pb.Codec;
import lark.pb.CodecFactory;

import java.util.List;

/**
 * 针对 lark-pb 包的 Google Protocol Buffers 解码器
 *
 * @author cuigh
 * @see io.netty.handler.codec.protobuf.ProtobufDecoder
 */
public class ProtobufDecoder extends MessageToMessageDecoder<ByteBuf> {
    private final Codec codec;

    /**
     * Creates a new instance.
     */
    @SuppressWarnings("unchecked")
    public ProtobufDecoder(Class<?> cls) {
        this.codec = CodecFactory.get(cls);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        final byte[] array;
        final int offset;
        final int length = msg.readableBytes();
        if (msg.hasArray()) {
            array = msg.array();
            offset = msg.arrayOffset() + msg.readerIndex();
        } else {
            array = new byte[length];
            msg.getBytes(msg.readerIndex(), array, 0, length);
            offset = 0;
        }

        out.add(codec.decode(array, offset, length));
    }
}
