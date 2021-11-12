package lark.net.rpc.server;

import lark.net.rpc.annotation.RpcMethod;
import lark.net.rpc.annotation.RpcParameter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务信息
 *
 * @author cuigh
 */
@Getter
@Setter
public class ServiceInfo {
    private String name;
    private String description;
    private Map<String, MethodInfo> methods;

    public ServiceInfo(Class<?> clazz, String name, String description) {
        this.name = name;
        this.description = description;
        this.methods = new HashMap<>();

        Method[] clazzMethods = clazz.getMethods();
        for (Method m : clazzMethods) {
            if (m.getDeclaringClass() == Object.class) {
                continue;
            }

            RpcMethod rpcMethod = m.getAnnotation(RpcMethod.class);
            MethodInfo mi = new MethodInfo();
            mi.name = ServiceContainer.getMethodName(m, rpcMethod);
            mi.description = rpcMethod == null ? null : rpcMethod.description();
            mi.returnType = getReturn(m);
            mi.parameters = getParameters(m);

            this.methods.put(mi.name, mi);
        }
    }

    private static List<ParameterInfo> getParameters(Method method) {
        Parameter[] parameters = method.getParameters();
        List<ParameterInfo> list = new ArrayList<>(parameters.length);
        if (parameters.length > 0) {
            for (Parameter p : parameters) {
                ParameterInfo pi = getParameter(p.getType(), p.getAnnotation(RpcParameter.class));
                if (StringUtils.isEmpty(pi.name)) {
                    pi.name = p.getName();
                }
                list.add(pi);
            }
        }
        return list;
    }

    private static ParameterInfo getReturn(Method method) {
        Class<?> returnType = method.getReturnType();
        if (returnType == null || returnType == void.class) {
            return null;
        }

        return getParameter(returnType, method.getAnnotation(RpcParameter.class));
    }

    private static ParameterInfo getParameter(Class<?> paramType, RpcParameter rpcParameter) {
        ParameterInfo pi = new ParameterInfo();
        pi.type = paramType;

        if (rpcParameter != null) {
            if (!StringUtils.isEmpty(rpcParameter.name())) {
                pi.name = rpcParameter.name();
            }
            pi.description = rpcParameter.description();
        }

        return pi;
    }

    /**
     * 方法信息
     */
    @Getter
    @Setter
    public static class MethodInfo {
        private String name;
        private String description;
        private List<ParameterInfo> parameters;
        private ParameterInfo returnType;
    }

    /**
     * 参数信息
     */
    @Getter
    @Setter
    public static class ParameterInfo {
        private String name;
        private Class<?> type;
        private String description;
    }

}
