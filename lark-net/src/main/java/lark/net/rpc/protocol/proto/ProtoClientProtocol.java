package lark.net.rpc.protocol.proto;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateHandler;
import lark.core.util.Arrays;
import lark.net.netty.codec.IdleHandler;
import lark.net.netty.codec.ProtobufDecoder;
import lark.net.rpc.RequestMessage;
import lark.net.rpc.client.ClientOptions;
import lark.net.rpc.protocol.ClientProtocol;
import lark.net.rpc.protocol.proto.data.Request;
import lark.net.rpc.protocol.proto.data.Response;
import lark.pb.Codec;
import lark.pb.CodecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cuigh
 */
public class ProtoClientProtocol implements ClientProtocol {
    private ClientOptions options;

    public ProtoClientProtocol(ClientOptions options) {
        this.options = options;
    }

    @Override
    public void initChannel(Channel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // write
        IdleStateHandler idleStateHandler = new IdleStateHandler((int) options.getIdleTime().getSeconds(), 0, 0);
        pipeline.addFirst("w.idle_state", idleStateHandler);
        pipeline.addFirst("w.idle_process", new IdleHandler(IdleState.WRITER_IDLE));
        pipeline.addFirst("w.encode", new RequestMessageEncoder());
        pipeline.addFirst("w.header", new LengthFieldPrepender(4));

        // read
        pipeline.addLast("r.frame", new LengthFieldBasedFrameDecoder(2 << 20, 0, 4));
        pipeline.addLast("r.decode", new ProtobufDecoder(Response.class));
        pipeline.addLast("r.process", new ClientHandler());
    }

    static class RequestMessageEncoder extends MessageToMessageEncoder<RequestMessage> {
        private static final Logger LOGGER = LoggerFactory.getLogger(RequestMessageEncoder.class);
        private Request msg = new Request();
        private Codec<Request> codec = CodecFactory.get(Request.class);

        @SuppressWarnings("unchecked")
        @Override
        protected void encode(ChannelHandlerContext ctx, RequestMessage m, List<Object> out) throws Exception {
            this.msg.setId(m.getId());
            this.msg.setClient("");
            this.msg.setService(m.getService());
            this.msg.setMethod(m.getMethod());
            if (!Arrays.isEmpty(m.getArgs())) {
                List<byte[]> params = new ArrayList<>(m.getArgs().length);
                for (Object arg : m.getArgs()) {
                    Codec c = CodecFactory.get(arg.getClass());
                    params.add(c.encode(arg));
                }
                this.msg.setArgs(params);
            }

            out.add(codec.encode(msg));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            ctx.close();
            LOGGER.error("Failed to encode request message", cause);
        }
    }
}
