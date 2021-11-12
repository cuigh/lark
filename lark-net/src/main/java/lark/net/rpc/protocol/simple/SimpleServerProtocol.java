package lark.net.rpc.protocol.simple;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateHandler;
import lark.net.netty.codec.IdleHandler;
import lark.net.netty.codec.ProtobufDecoder;
import lark.net.rpc.protocol.ServerProtocol;
import lark.net.rpc.protocol.simple.data.SimpleRequestMessage;
import lark.net.rpc.protocol.simple.data.SimpleResponseMessage;
import lark.net.rpc.server.InvokeResult;
import lark.net.rpc.server.Server;
import lark.pb.Codec;
import lark.pb.CodecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author cuigh
 */
public class SimpleServerProtocol implements ServerProtocol {
    private static final String HEADER = "SimpleRequest ";
    private Server server;

    public SimpleServerProtocol(Server server) {
        this.server = server;
    }

    @Override
    public boolean match(ChannelHandlerContext ctx, ByteBuf in) {
        // "SimpleRequest" + ' ' + [length] + "\r\n" + [data]
        if (in.readableBytes() >= HEADER.length()) {
            CharSequence cs = in.getCharSequence(0, HEADER.length(), StandardCharsets.US_ASCII);
            return HEADER.contentEquals(cs);
        }
        return false;
    }

    @Override
    public void initChannnel(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();

        // read
        int idleTime = (int) server.getOptions().getIdleTime().getSeconds();
        IdleStateHandler idleStateHandler = new IdleStateHandler(idleTime, 0, 0);
        pipeline.addLast("r.idle_state", idleStateHandler);
        pipeline.addLast("r.idle_process", new IdleHandler(IdleState.READER_IDLE));
        pipeline.addLast("r.frame", new SimpleFrameDecoder("SimpleRequest"));
        pipeline.addLast("r.decode", new ProtobufDecoder(SimpleRequestMessage.class));
        pipeline.addLast("r.process", new ServerHandler(server));

        // write
        pipeline.addLast("w.header", new SimpleHeaderPrepender("SimpleResponse"));
        pipeline.addLast("w.encode", new ResponseMessageEncoder());
    }

    static class ResponseMessageEncoder extends MessageToMessageEncoder<InvokeResult> {
        private static final Logger LOGGER = LoggerFactory.getLogger(ResponseMessageEncoder.class);
        private SimpleResponseMessage msg = new SimpleResponseMessage();
        private Codec<SimpleResponseMessage> codec = CodecFactory.get(SimpleResponseMessage.class);

        @Override
        @SuppressWarnings("unchecked")
        protected void encode(ChannelHandlerContext ctx, InvokeResult r, List out) throws Exception {
            this.msg.setId(Long.toString(r.getId()));
            if (r.getError() == null) {
                this.msg.fillResult(r.getValue());
            } else {
                this.msg.fillError(r.getError());
            }
            out.add(codec.encode(this.msg));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            ctx.close();
            LOGGER.error("Failed to encode response message", cause);
        }
    }
}
