package lark.net.rpc.client;

import lark.net.rpc.annotation.RpcMethod;
import lark.net.rpc.annotation.RpcService;
import lark.net.rpc.server.ServiceContainer;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cuigh
 */
public class MethodInfo {
    private static final Map<Class<?>, Map<String, MethodInfo>> CACHES = new ConcurrentHashMap<>();
    private String service;
    private String name;
    private Class<?> returnType;
    private InvokeMode invokeMode = InvokeMode.FAIL_OVER;

    public MethodInfo(String service, Method method, RpcMethod rpcMethod, RpcService rpcService) {
        this.service = service;
        this.name = ServiceContainer.getMethodName(method, rpcMethod);
        this.returnType = method.getReturnType();
        if (rpcMethod != null) {
            this.invokeMode = rpcMethod.invoke();
        } else if (rpcService != null) {
            this.invokeMode = rpcService.invoke();
        }
    }

    public static Map<String, MethodInfo> get(Class<?> clazz) {
        return CACHES.computeIfAbsent(clazz, cls -> {
            String service;
            Map<String, MethodInfo> methods = new HashMap<>();

            RpcService rpcService = clazz.getAnnotation(RpcService.class);
            if (rpcService == null || StringUtils.isEmpty(rpcService.name())) {
                service = clazz.getSimpleName();
            } else {
                service = rpcService.name();
            }

            Method[] clazzMethods = clazz.getMethods();
            for (Method m : clazzMethods) {
                if (m.getDeclaringClass() == Object.class) {
                    continue;
                }

                RpcMethod rpcMethod = m.getAnnotation(RpcMethod.class);
                methods.put(m.getName(), new MethodInfo(service, m, rpcMethod, rpcService));
            }
            return methods;
        });
    }

    public String getService() {
        return service;
    }

    public String getName() {
        return name;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public InvokeMode getInvokeMode() {
        return invokeMode;
    }
}
