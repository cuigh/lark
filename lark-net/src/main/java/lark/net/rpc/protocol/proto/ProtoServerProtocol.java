package lark.net.rpc.protocol.proto;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateHandler;
import lark.core.lang.BusinessException;
import lark.net.netty.codec.IdleHandler;
import lark.net.netty.codec.ProtobufDecoder;
import lark.net.rpc.RpcError;
import lark.net.rpc.protocol.ServerProtocol;
import lark.net.rpc.protocol.proto.data.Error;
import lark.net.rpc.protocol.proto.data.Request;
import lark.net.rpc.protocol.proto.data.Response;
import lark.net.rpc.server.InvokeResult;
import lark.net.rpc.server.Server;
import lark.pb.Codec;
import lark.pb.CodecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author cuigh
 */
public class ProtoServerProtocol implements ServerProtocol {
    private Server server;

    public ProtoServerProtocol(Server server) {
        this.server = server;
    }

    @Override
    public boolean match(ChannelHandlerContext ctx, ByteBuf in) {
        return true;
    }

    @Override
    public void initChannnel(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();

        // read
        int idleTime = (int) server.getOptions().getIdleTime().getSeconds();
        IdleStateHandler idleStateHandler = new IdleStateHandler(idleTime, 0, 0);
        pipeline.addLast("r.idle_state", idleStateHandler);
        pipeline.addLast("r.idle_process", new IdleHandler(IdleState.READER_IDLE));
        pipeline.addLast("r.frame", new LengthFieldBasedFrameDecoder(2 << 20, 0, 4));
        pipeline.addLast("r.decode", new ProtobufDecoder(Request.class));
        pipeline.addLast("r.process", new ServerHandler(server));

        // write
        pipeline.addLast("w.header", new LengthFieldPrepender(4));
        pipeline.addLast("w.encode", new ResponseMessageEncoder());
    }

    static class ResponseMessageEncoder extends MessageToMessageEncoder<InvokeResult> {
        private static final Logger LOGGER = LoggerFactory.getLogger(ResponseMessageEncoder.class);
        private Response msg = new Response();
        private Codec<Response> codec = CodecFactory.get(Response.class);

        @SuppressWarnings("unchecked")
        @Override
        protected void encode(ChannelHandlerContext ctx, InvokeResult r, List<Object> out) throws Exception {
            this.msg.setId(r.getId());
            if (r.getError() == null) {
                Codec c = CodecFactory.get(r.getValue().getClass());
                this.msg.setResult(c.encode(r.getValue()));
            } else {
                Error error = new Error();
                if (r.getError() instanceof BusinessException) {
                    error.setCode(((BusinessException) r.getError()).getCode());
                } else {
                    error.setCode(RpcError.SERVER_UNKNOWN_ERROR.value());
                }
                error.setMessage(r.getError().getMessage());
                if (StringUtils.isEmpty(error.getMessage())) {
                    error.setMessage(r.getError().toString());
                }
//                error.setDetail(Exceptions.getStackTrace(e));
                this.msg.setError(error);
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
