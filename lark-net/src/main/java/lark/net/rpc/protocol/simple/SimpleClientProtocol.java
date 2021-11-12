package lark.net.rpc.protocol.simple;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lark.core.util.Arrays;
import lark.net.netty.codec.IdleHandler;
import lark.net.netty.codec.ProtobufDecoder;
import lark.net.rpc.RequestMessage;
import lark.net.rpc.client.ClientOptions;
import lark.net.rpc.protocol.ClientProtocol;
import lark.net.rpc.protocol.simple.data.SimpleLabel;
import lark.net.rpc.protocol.simple.data.SimpleRequestMessage;
import lark.net.rpc.protocol.simple.data.SimpleResponseMessage;
import lark.net.rpc.protocol.simple.data.SimpleValue;
import lark.pb.Codec;
import lark.pb.CodecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * @author cuigh
 */
public class SimpleClientProtocol implements ClientProtocol {
    private ClientOptions options;

    public SimpleClientProtocol(ClientOptions options) {
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
        pipeline.addFirst("w.header", new SimpleHeaderPrepender("SimpleRequest"));

        // read
        pipeline.addLast("r.frame", new SimpleFrameDecoder("SimpleResponse"));
        pipeline.addLast("r.decode", new ProtobufDecoder(SimpleResponseMessage.class));
        pipeline.addLast("r.process", new ClientHandler());
    }

    static class RequestMessageEncoder extends MessageToMessageEncoder<RequestMessage> {
        private static final Logger LOGGER = LoggerFactory.getLogger(RequestMessageEncoder.class);
        private SimpleRequestMessage msg = new SimpleRequestMessage();
        private Codec<SimpleRequestMessage> codec = CodecFactory.get(SimpleRequestMessage.class);

        @Override
        @SuppressWarnings("unchecked")
        protected void encode(ChannelHandlerContext ctx, RequestMessage m, List out) throws Exception {
            this.msg.setId(Long.toString(m.getId()));
            this.msg.setClient("");
            this.msg.setService(m.getService());
            this.msg.setMethod(m.getMethod());
            this.msg.setLabels(getLabels(m));
            if (Arrays.isEmpty(m.getArgs())) {
                this.msg.setArgs(null);
            } else {
                List<SimpleValue> parameters = new ArrayList<>(m.getArgs().length);
                for (Object arg : m.getArgs()) {
                    SimpleValue value = SimpleEncoder.encode(arg);
                    parameters.add(value);
                }
                this.msg.setArgs(parameters);
            }

            out.add(codec.encode(this.msg));
        }

        private List<SimpleLabel> getLabels(RequestMessage m) {
            if (CollectionUtils.isEmpty(m.getLabels())) return null;
            return m.getLabels().entrySet().stream().map(this::toSimpleLabel).collect(Collectors.toList());
        }

        private SimpleLabel toSimpleLabel(Map.Entry<String,String> map) {
            SimpleLabel simpleLabel = new SimpleLabel();
            simpleLabel.setName(map.getKey());
            simpleLabel.setValue(map.getValue());
            return  simpleLabel;
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            ctx.close();
            LOGGER.error("Failed to encode request message", cause);
        }
    }
}
