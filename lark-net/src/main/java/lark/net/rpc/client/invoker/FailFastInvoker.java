package lark.net.rpc.client.invoker;

import lark.net.rpc.client.balancer.Balancer;
import lark.net.rpc.client.MethodInfo;
import lark.net.rpc.client.Node;

/**
 * @author cuigh
 */
public class FailFastInvoker implements Invoker {
    @Override
    public Object invoke(Balancer balancer, MethodInfo method, Object[] args) {
        Node node = balancer.select();
        return node.invoke(method.getService(), method.getName(), args, method.getReturnType());
    }
}
