package lark.net.rpc.client.invoker;

import lark.net.rpc.client.balancer.Balancer;
import lark.net.rpc.client.Node;

import java.util.List;

/**
 * @author cuigh
 */
public class InvokeContext {
    List<Node> nodes;
    Balancer balancer;
    String service;
    String method;
    Object[] args;
    Class<?> returnType;
//    RequestMessage request;
}
