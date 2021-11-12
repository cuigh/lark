package lark.net.rpc.server.filter;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lark.core.lang.BusinessException;
import lark.net.rpc.RequestMessage;
import lark.net.rpc.server.InvokeContext;
import lark.net.rpc.server.InvokeHandler;

/**
 * A metrics filter based on Spring Actuator
 *
 * @author cuigh
 */
public class MetricsHandler implements InvokeHandler {
    private MeterRegistry registry;
    private InvokeHandler inner;
    private double[] percentiles = {0.5, 0.9, 0.99};

    public MetricsHandler(MeterRegistry registry, InvokeHandler handler) {
        this.registry = registry;
        this.inner = handler;
    }

    public void setPercentiles(double... percentiles) {
        this.percentiles = percentiles;
    }

    @Override
    public Object handle(InvokeContext ctx) {
        int code = 1;
        Timer.Sample sample = Timer.start(registry);
        try {
            Object result = this.inner.handle(ctx);
            code = 0;
            return result;
        } catch (BusinessException e) {
            code = e.getCode();
            throw e;
        } finally {
            RequestMessage req = ctx.getRequest();
            getCounter(req.getService(), req.getMethod(), code).increment();
            sample.stop(getTimer(req.getService(), req.getMethod()));
        }
    }

    private Counter getCounter(String service, String method, int code) {
        return Counter.builder("rpc.server.requests.total")
                .description("How many RPC requests processed, partitioned by service and method")
                .tags("service", service, "method", method, "code", Integer.toString(code))
                .register(registry);
    }

    private Timer getTimer(String service, String method) {
        return Timer.builder("rpc.server.request.duration")
                .description("The RPC request latencies in seconds, partitioned by service and method")
                .tags("service", service, "method", method)
//                .publishPercentileHistogram()
                .publishPercentiles(percentiles)
                .register(registry);
    }
}
