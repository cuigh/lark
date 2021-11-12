package lark.net.rpc.client;

import lark.net.rpc.client.resolver.Resolver;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cuigh
 */
public class ServiceFactory {
    private final Map<Class, Object> proxies = new ConcurrentHashMap<>();
    private final Map<String, Client> clients = new ConcurrentHashMap<>();
    private final ServiceClassLoader classLoader = new ServiceClassLoader(Thread.currentThread().getContextClassLoader());

    /**
     * Client name
     */
    private String name;
    private Resolver resolver;
    private Map<String, ClientOptions> options = new HashMap<>();
    private List<ClientFilter> filters = new ArrayList<>();

    /**
     * 获取编解码器
     *
     * @param server 服务端名称
     * @param cls    目标类信息
     * @param <T>    数据类型
     * @return 编解码器
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String server, Class<T> cls) {
        return (T) proxies.computeIfAbsent(cls, c -> createProxy(server, cls));
    }

    private Object createProxy(String server, Class cls) {
        if (!cls.isInterface()) {
            throw new IllegalArgumentException("Can't create proxy for non-interface type: " + cls.getName());
        }

        String proxyTypeName = ServiceClassLoader.getProxyClassName(cls);
        try {
            Class<?> proxyClass = classLoader.loadProxyClass(proxyTypeName, cls);
            Constructor<?> constructor = proxyClass.getConstructor(Client.class);
            Client client = clients.computeIfAbsent(server, this::createClient);
            return constructor.newInstance(client);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private Client createClient(String server) {
        ClientOptions options = this.options.get(server);
        if (options == null) {
            options = new ClientOptions();
        }
        Client client = new Client(server, options);
        client.setResolver(resolver);
        client.setFilters(filters);
        return client;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setResolver(Resolver resolver) {
        this.resolver = resolver;
    }

    public void addOptions(String name, ClientOptions options) {
        this.options.put(name, options);
    }

    public void use(ClientFilter... filters) {
        Collections.addAll(this.filters, filters);
    }
}
