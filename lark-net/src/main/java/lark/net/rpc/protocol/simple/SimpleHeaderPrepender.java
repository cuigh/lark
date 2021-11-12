package lark.net.rpc.protocol.simple;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @author cuigh
 */
public class SimpleHeaderPrepender extends MessageToByteEncoder<byte[]> {
    private static final byte[] CRLF = new byte[]{'\r', '\n'};
    private final byte[] header;

    public SimpleHeaderPrepender(String header) {
        this.header = (header + " ").getBytes(StandardCharsets.US_ASCII);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf out) throws Exception {
        // "[header] " + [length] + "\r\n" + [data]
        out.writeBytes(header);
        out.writeBytes(Integer.toString(msg.length).getBytes(StandardCharsets.US_ASCII));
        out.writeBytes(CRLF);
        out.writeBytes(msg);
    }
}
