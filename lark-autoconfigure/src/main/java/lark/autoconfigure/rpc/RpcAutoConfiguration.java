package lark.autoconfigure.rpc;

import io.micrometer.core.instrument.MeterRegistry;
import lark.autoconfigure.etcd.Etcd2AutoConfiguration;
import lark.core.app.registry.Collector;
import lark.core.util.Networks;
import lark.net.rpc.RpcApplication;
import lark.net.rpc.RpcCollector;
import lark.net.rpc.client.ClientOptions;
import lark.net.rpc.client.ServiceFactory;
import lark.net.rpc.client.resolver.EtcdResolver;
import lark.net.rpc.client.resolver.Resolver;
import lark.net.rpc.server.Server;
import lark.net.rpc.server.ServerOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.SocketUtils;
import org.springframework.util.StringUtils;
import org.zalando.boot.etcd.EtcdClient;

import java.util.Map;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for RPC client and server.
 *
 * @author cuigh
 */
@Configuration
@AutoConfigureAfter(Etcd2AutoConfiguration.class)
@ConditionalOnClass(RpcApplication.class)
public class RpcAutoConfiguration {
    private final ApplicationContext ctx;
    @Autowired(required = false)
    private MeterRegistry meterRegistry;
    @Autowired(required = false)
    private Resolver resolver;

    public RpcAutoConfiguration(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Bean
    @ConditionalOnMissingBean
    public ServiceFactory rpcServiceFactory(RpcClientProperties properties) {
        ServiceFactory factory = new ServiceFactory();
        if (properties.client != null) {
            properties.client.forEach(factory::addOptions);
        }
        factory.setName(ctx.getId());
        factory.setResolver(resolver);
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty("lark.rpc.server.address")
    @ConfigurationProperties(prefix = "lark.rpc.server")
    public ServerOptions rpcServerOptions() {
        return new ServerOptions();
    }

//    @Bean
//    @ConditionalOnBean(EtcdClient.class)
//    @ConditionalOnMissingBean
//    public Register rpcEtcdRegister(EtcdClient etcdClient, TaskScheduler scheduler) {
//        EtcdRegister registry = new EtcdRegister(etcdClient, scheduler);
//        registry.setServerAddress(findServerAddress());
//        return registry;
//    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(EtcdClient.class)
    public Resolver rpcEtcdResolver(EtcdClient etcdClient, TaskScheduler scheduler) {
        return new EtcdResolver(etcdClient, scheduler);
    }

    @Bean
    @ConditionalOnBean(ServerOptions.class)
    @ConditionalOnMissingBean
    public Server rpcServer(ServerOptions options) {
        if (StringUtils.isEmpty(options.getName())) {
            options.setName(ctx.getId());
        }
        options.setAddress(ensureAddress(options.getAddress()));
        Server server = new Server(options);
        server.setMeterRegistry(meterRegistry);
        return server;
    }

    @Bean
    @ConditionalOnBean(Server.class)
    public Collector rpcCollector(Server server) {
        RpcCollector collector = new RpcCollector(server);
        collector.setServerAddress(findServerAddress());
        return collector;
    }

    private String ensureAddress(String address) {
        String[] parts = address.split(":");
        int port = 0;
        if (parts.length > 1 && !StringUtils.isEmpty(parts[1])) {
            port = Integer.parseInt(parts[1]);
        }
        if (port == 0) {
            // 如果没有指定端口，则使用 [20000, 30000] 内的随机端口
            port = SocketUtils.findAvailableTcpPort(20000, 30000);
        }
        return String.format("%s:%d", parts[0], port);
    }

    private String findServerAddress() {
        String address = ctx.getEnvironment().getProperty("server.address");
        if (StringUtils.isEmpty(address)) {
            String prefix = ctx.getEnvironment().getProperty("server.address-prefix");
            address = Networks.findLocalIP4(prefix);
        }
        return address;
    }

    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "lark.rpc")
    static class RpcClientProperties {
        private Map<String, ClientOptions> client;

        public void setClient(Map<String, ClientOptions> client) {
            this.client = client;
        }
    }
}
