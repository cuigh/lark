package lark.net.rpc.client;

import lark.net.rpc.annotation.RpcMethod;
import lark.net.rpc.annotation.RpcService;
import lark.net.rpc.server.ServiceContainer;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cuigh
 */
public class ServiceMeta {
    private static final Map<Class<?>, ServiceMeta> METAS = new HashMap<>();
    private String serviceName;
    private Map<String, MethodInfo> methods = new HashMap<>();

    public ServiceMeta(Class<?> clazz) {
        RpcService rpcService = clazz.getAnnotation(RpcService.class);
        if (rpcService == null || StringUtils.isEmpty(rpcService.name())) {
            this.serviceName = clazz.getSimpleName();
        } else {
            this.serviceName = rpcService.name();
        }

        Method[] clazzMethods = clazz.getMethods();
        for (Method m : clazzMethods) {
            if (m.getDeclaringClass() == Object.class) {
                continue;
            }

            RpcMethod rpcMethod = m.getAnnotation(RpcMethod.class);
            String methodName = ServiceContainer.getMethodName(m, rpcMethod);
            this.methods.put(m.getName(), new MethodInfo(this.serviceName, m, rpcMethod, rpcService));
        }
    }

    public static synchronized ServiceMeta get(Class<?> clazz) {
        ServiceMeta meta = METAS.get(clazz);
        if (meta == null) {
            meta = new ServiceMeta(clazz);
            METAS.put(clazz, meta);
        }
        return meta;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Map<String, MethodInfo> getMethods() {
        return methods;
    }

    public void setMethods(Map<String, MethodInfo> methods) {
        this.methods = methods;
    }
}
