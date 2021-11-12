package lark.net.rpc.client.filter;

import lark.net.rpc.RequestMessage;
import lark.net.rpc.client.Call;
import lark.net.rpc.client.CallHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cuigh
 */
public class StatsHandler implements CallHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatsHandler.class);

    private CallHandler inner;

    public StatsHandler(CallHandler handler) {
        this.inner = handler;
    }

    @Override
    public Object handle(Call call) {
        long start = System.currentTimeMillis();
        boolean success = false;
        try {
            Object result = this.inner.handle(call);
            success = true;
            return result;
        } finally {
            RequestMessage req = call.getRequest();
            LOGGER.info("id:{}, server:{}, service:{}, method:{}, success:{}, time:{}ms",
                    req.getId(),
                    req.getServer(),
                    req.getService(),
                    req.getMethod(),
                    success,
                    System.currentTimeMillis() - start);
        }
    }
}
