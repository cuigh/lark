package lark.net.rpc.client.filter;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.tag.Tags;
import lark.core.lang.BusinessException;
import lark.net.rpc.RpcError;
import lark.net.rpc.client.Call;
import lark.net.rpc.client.CallHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author niantian
 */
public class TraceHandler implements CallHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraceHandler.class);
    public static final String RPC_CLIENT = "rpc/java-client";
    public static final String TAG_RPC_STATUS_CODE = "rpc.status_code";
    public static final String TAG_RPC_SERVER = "rpc.server";
    private final Tracer tracer;
    private final CallHandler inner;

    public TraceHandler(CallHandler inner, Tracer tracer) {
        this.inner = inner;
        this.tracer = tracer;
    }

    @Override
    public Object handle(Call call) {
        String name = call.getRequest().getService() + "." + call.getRequest().getMethod();
        Span span = getSpan(name)
                .setTag(Tags.COMPONENT, RPC_CLIENT)
                .setTag(Tags.SPAN_KIND, Tags.SPAN_KIND_CLIENT)
                .setTag(TAG_RPC_SERVER, call.getRequest().getServer());

        Map<String, String> labels = call.getRequest().getLabels();
        if (labels == null) {
            labels = new HashMap<>();
            call.getRequest().setLabels(labels);
        }
        tracer.inject(span.context(), Format.Builtin.TEXT_MAP, new TextMapAdapter(labels));
        LOGGER.debug("trace info: {}", labels);

        Object result;
        int code = 0;
        try (Scope scope = tracer.scopeManager().activate(span)) {
            result = inner.handle(call);
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

    private Span getSpan(String name) {
        Span span = tracer.activeSpan();
        if (span == null) {
            return tracer.buildSpan(name).start();
        } else {
            return tracer.buildSpan(name).asChildOf(span).start();
        }
    }

    private int getCode(Exception e) {
        if (e instanceof BusinessException) {
            return ((BusinessException) e).getCode();
        }
        return RpcError.CLIENT_UNKNOWN_ERROR.value();
    }
}
