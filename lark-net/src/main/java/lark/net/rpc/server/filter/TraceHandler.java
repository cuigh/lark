package lark.net.rpc.server.filter;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.tag.Tags;
import lark.core.lang.BusinessException;
import lark.net.rpc.RpcError;
import lark.net.rpc.server.InvokeContext;
import lark.net.rpc.server.InvokeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Map;

import static lark.net.rpc.client.filter.TraceHandler.TAG_RPC_STATUS_CODE;

/**
 * @author niantian
 */
public class TraceHandler implements InvokeHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraceHandler.class);
    public static final String RPC_SERVER = "rpc/java-server";
    private final Tracer tracer;
    private final InvokeHandler inner;

    public TraceHandler(Tracer tracer, InvokeHandler inner) {
        this.tracer = tracer;
        this.inner = inner;
    }

    @Override
    public Object handle(InvokeContext ctx) {
        final String name = ctx.getRequest().getService() + "." + ctx.getRequest().getMethod();
        Span span = startServerSpan(tracer, name, ctx.getRequest().getLabels())
                .setTag(Tags.SPAN_KIND, Tags.SPAN_KIND_SERVER);

        Object result;
        int code = 0;
        try (Scope scope = tracer.scopeManager().activate(span)) {
            result = inner.handle(ctx);
        } catch (Exception e) {
            code = getCode(e);
            span.setTag(Tags.ERROR, true);
            throw e;
        } finally {
            span.setTag(TAG_RPC_STATUS_CODE, code);
            span.finish();
        }
        return result;
    }

    private static Span startServerSpan(Tracer tracer, String name, Map<String, String> labels) {
        Tracer.SpanBuilder spanBuilder;
        if (CollectionUtils.isEmpty(labels)) {
            spanBuilder = tracer.buildSpan(name);
        } else {
            spanBuilder = getSpanBuilder(tracer, name, labels);
        }
        return spanBuilder.withTag(Tags.COMPONENT, RPC_SERVER).start();
    }

    private static Tracer.SpanBuilder getSpanBuilder(Tracer tracer, String name, Map<String, String> labels) {
        Tracer.SpanBuilder spanBuilder;
        try {
            SpanContext parentSpanCtx = tracer.extract(Format.Builtin.TEXT_MAP, new TextMapAdapter(labels));
            if (parentSpanCtx == null) {
                spanBuilder = tracer.buildSpan(name);
            } else {
                spanBuilder = tracer.buildSpan(name).asChildOf(parentSpanCtx);
            }
        } catch (Exception e) {
            LOGGER.error("server trace is failure cause: ", e);
            spanBuilder = tracer.buildSpan(name);
        }
        return spanBuilder;
    }

    private int getCode(Exception e) {
        if (e instanceof BusinessException) {
            return ((BusinessException) e).getCode();
        }
        return RpcError.SERVER_UNKNOWN_ERROR.value();
    }
}
