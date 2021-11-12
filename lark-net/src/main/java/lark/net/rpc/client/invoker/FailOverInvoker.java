package lark.net.rpc.client.invoker;

import lark.core.context.Context;
import lark.core.lang.BusinessException;
import lark.net.rpc.RpcError;
import lark.net.rpc.RpcException;
import lark.net.rpc.client.MethodInfo;
import lark.net.rpc.client.Node;
import lark.net.rpc.client.balancer.Balancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cuigh
 */
public class FailOverInvoker implements Invoker {
    private static final Logger LOGGER = LoggerFactory.getLogger(FailOverInvoker.class);
    /**
     * TODO: read option from config
     */
    private final int maxRetry = 2;

    @Override
    public Object invoke(Balancer balancer, MethodInfo method, Object[] args) {
        BusinessException lastError;
        Node first = balancer.select();
        try {
            return first.invoke(method.getService(), method.getName(), args, method.getReturnType());
        } catch (BusinessException e) {
            if (this.maxRetry == 0) {
                throw e;
            }

            checkError(e);
            lastError = e;
        }

        // 如果失败则依次重试其它节点
        int retry = this.maxRetry;
        Node node = first;
        while (retry-- > 0) {
            if (Context.get().getDeadline() <= System.currentTimeMillis()) {
                throw new RpcException(RpcError.CLIENT_DEADLINE_EXCEEDED);
            }

            node = node.getNext();
            if (node == first) {
                break;
            }

            try {
                return node.invoke(method.getService(), method.getName(), args, method.getReturnType());
            } catch (BusinessException e) {
                checkError(e);
                lastError = e;
            }
        }

        throw lastError;
    }


    private void checkError(BusinessException e) {
        int errorCode = e.getCode();
        if (!RpcError.isRpcError(errorCode)) {
            throw e;
        }
//        LOGGER.warn("Invoke failed: {}[code={}, server={}]", e.getMessage(), e.getCode(), container.options.getName());
    }
}
