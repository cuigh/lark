package lark.net.rpc.protocol.simple;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author cuigh
 */
public class SimpleFrameDecoder extends ByteToMessageDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFrameDecoder.class);
    private final String header;
    private final int maxLength;
    private int length = -1;

    public SimpleFrameDecoder(String header) {
        this(header, 2 << 20);
    }

    public SimpleFrameDecoder(String header, int maxLength) {
        this.header = header;
        this.maxLength = maxLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // "[header]" + ' ' + [length] + "\r\n" + [data]
        if (length == -1) {
            decodeHeader(ctx, in, out);
        } else if (length == 0) {
            decodeLength(ctx, in, out);
        } else {
            decodeBody(in, out);
        }
    }

    private void decodeHeader(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) {
        if (buf.readableBytes() > header.length()) {
            if (buf.getByte(header.length()) == ' ') {
                buf.skipBytes(header.length() + 1);
                length = 0;
                decodeLength(ctx, buf, out);
            } else {
                LOGGER.warn("Wrong protocol, expect '{} ' as header", header);
                ctx.close();
            }
        }
    }

    private void decodeLength(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) {
        if (buf.readableBytes() > 0) {
            int number = buf.bytesBefore((byte) '\n');
            if (number > 0) {
                CharSequence cs = buf.readCharSequence(number - 1, StandardCharsets.US_ASCII);
                length = Integer.parseInt(cs.toString());
                if (length > maxLength) {
                    LOGGER.warn("Exceed max message size: {}", maxLength);
                    ctx.close();
                } else {
                    buf.skipBytes(2);
                    decodeBody(buf, out);
                }
            }
        }
    }

    private void decodeBody(ByteBuf buf, List<Object> out) {
        if (buf.readableBytes() >= length) {
            int index = buf.readerIndex();
            ByteBuf slice = buf.retainedSlice(index, length);
            buf.readerIndex(index + length);
            length = -1;
            out.add(slice);
        }
    }
}
