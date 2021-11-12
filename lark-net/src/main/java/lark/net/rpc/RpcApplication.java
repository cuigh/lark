package lark.net.rpc;

import lark.core.app.Application;
import lark.net.rpc.annotation.RpcService;
import lark.net.rpc.client.BaseService;
import lark.net.rpc.protocol.ServerProtocol;
import lark.net.rpc.server.Server;
import lark.net.rpc.server.ServerFilter;
import lark.net.rpc.server.register.Register;
import lark.net.rpc.service.MetaService;
import lark.net.rpc.service.MetaServiceImp;
import lark.net.rpc.service.SystemService;
import lark.net.rpc.service.SystemServiceImp;
import org.springframework.core.io.ResourceLoader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author cuigh
 */
public class RpcApplication extends Application {
    protected Server server;

    public RpcApplication(Class<?>... primarySources) {
        this(null, primarySources);
    }

    public RpcApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
        super(resourceLoader, primarySources);
    }

    @Override
    protected void load() {
        super.load();
        this.server = ctx.getBean(Server.class);

        // Set Register
        Register register = findBean(Register.class);
        if (register != null) {
            server.setRegister(register);
        }

        // Register protocols
        Map<String, ServerProtocol> protocols = ctx.getBeansOfType(ServerProtocol.class);
        this.server.registerProtocol(protocols.values().toArray(new ServerProtocol[0]));

        // Register filters
        Map<String, ServerFilter> filters = ctx.getBeansOfType(ServerFilter.class);
        this.server.use(filters.values().toArray(new ServerFilter[0]));

        registerServices();
    }

    @Override
    protected void start() {
        this.server.start();
    }

    private void registerServices() {
        // 注册系统服务
        this.server.registerService(SystemService.class, new SystemServiceImp(server, ctx.getStartupDate()));
        this.server.registerService(MetaService.class, new MetaServiceImp(server));

        // 注册自定义服务
        Map<String, Object> beans = ctx.getBeansWithAnnotation(RpcService.class);
        beans.forEach((name, bean) -> {
            if (!(bean instanceof BaseService)) {
                List<Class<?>> classes = Arrays.stream(bean.getClass().getInterfaces()).
                        filter(i -> i.isAnnotationPresent(RpcService.class)).collect(Collectors.toList());
                if (classes.isEmpty()) {
                    server.registerService(bean);
                } else {
                    classes.forEach(c -> server.registerService(c, bean));
                }
            }
        });
    }
}
