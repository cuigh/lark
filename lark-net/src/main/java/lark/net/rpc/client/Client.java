package lark.net.rpc.client;

import lark.core.context.Context;
import lark.core.util.Strings;
import lark.net.Address;
import lark.net.rpc.RpcError;
import lark.net.rpc.RpcException;
import lark.net.rpc.client.balancer.Balancer;
import lark.net.rpc.client.balancer.BalancerFactory;
import lark.net.rpc.client.invoker.FailFastInvoker;
import lark.net.rpc.client.invoker.FailOverInvoker;
import lark.net.rpc.client.invoker.Invoker;
import lark.net.rpc.client.resolver.AddressHolder;
import lark.net.rpc.client.resolver.Resolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author cuigh
 */
public class Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    private String server;
    private ClientOptions options;
    private Balancer balancer;
    private List<Node> nodes;
    private Resolver resolver;
    private List<ClientFilter> filters;
    private Invoker failFast = new FailFastInvoker();
    private Invoker failOver = new FailOverInvoker();

    public Client(String server, ClientOptions options) {
        Objects.requireNonNull(options);
        this.server = server;
        this.options = options;
        this.balancer = BalancerFactory.create(options.getBalancer());
    }

    public void setResolver(Resolver resolver) {
        this.resolver = resolver;
    }

    public void setFilters(List<ClientFilter> filters) {
        this.filters = filters;
    }

    public Object invoke(MethodInfo method, Object[] args) {
        if (this.nodes == null) {
            initNodes();
        }
        Context.setDeadline(System.currentTimeMillis() + options.getReadTimeout().toMillis());
        return getInvoker(method.getInvokeMode()).invoke(balancer, method, args);
    }

    private synchronized void initNodes() {
        if (this.nodes != null) {
            return;
        }

        List<Address> addresses = resolve();
        if (addresses.isEmpty()) {
            throw new RpcException(RpcError.CLIENT_NO_PROVIDER, server);
        }

        this.updateNodes(addresses);
    }

    private List<Address> resolve() {
        List<Address> list = new ArrayList<>();

        if (resolver != null) {
            AddressHolder holder = resolver.resolve(server, options.getVersion());
            holder.subscribe(this::updateNodes);
            List<Address> addresses = holder.get();
            if (CollectionUtils.isEmpty(addresses)) {
                LOGGER.warn("从注册中心未获取到任何属于服务 {} 的节点", server);
            } else {
                list.addAll(addresses);
            }
        }

        if (list.isEmpty() && !Strings.isEmpty(options.getAddress())) {
            LOGGER.info("使用地址 {} 直连 {} ", options.getAddress(), server);
            Address address = new Address();
            address.setUrl(options.getAddress());
            list.add(address);
        }

        return list;
    }

    /**
     * 刷新节点
     */
    private void updateNodes(List<Address> addresses) {
        List<Node> list;
        if (addresses.isEmpty()) {
            list = Collections.emptyList();
        } else {
            list = addresses.stream().map(this::createNode).collect(Collectors.toList());
            for (int i = 0; i < list.size() - 1; i++) {
                list.get(i).setNext(list.get(i + 1));
            }
            list.get(list.size() - 1).setNext(list.get(0));
        }

        this.nodes = list;
        this.balancer.setNodes(list);
    }

    private Node createNode(Address address) {
        return new Node(server, address, options, this.filters);
    }

    private Invoker getInvoker(InvokeMode failMode) {
        if (failMode == InvokeMode.FAIL_FAST) {
            return failFast;
        }
        return failOver;
    }
}
