package lark.net.rpc.server.filter;

import lark.net.rpc.RequestMessage;
import lark.net.rpc.server.InvokeContext;
import lark.net.rpc.server.InvokeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cuigh
 */
public class StatsHandler implements InvokeHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatsHandler.class);
    private InvokeHandler inner;

    public StatsHandler(InvokeHandler handler) {
        this.inner = handler;
    }

    @Override
    public Object handle(InvokeContext ctx) {
        long start = System.currentTimeMillis();
        boolean success = false;
        try {
            Object result = this.inner.handle(ctx);
            success = true;
            return result;
        } finally {
            RequestMessage req = ctx.getRequest();
            LOGGER.info("id:{}, client:{}, service:{}, method:{}, success:{}, time:{}ms",
                    req.getId(),
                    req.getClient(),
                    req.getService(),
                    req.getMethod(),
                    success,
                    System.currentTimeMillis() - start);
        }
    }
}
