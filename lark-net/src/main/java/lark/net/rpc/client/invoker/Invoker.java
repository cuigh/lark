package lark.net.rpc.client.invoker;

import lark.net.rpc.client.balancer.Balancer;
import lark.net.rpc.client.MethodInfo;

/**
 * @author cuigh
 */
public interface Invoker {
    Object invoke(Balancer balancer, MethodInfo method, Object[] args);
}
