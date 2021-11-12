package lark.net.rpc.server;

import com.esotericsoftware.reflectasm.MethodAccess;

/**
 * @author cuigh
 */
public class ServiceMethod {
    private MethodAccess access;
    private int index;
    private Object object;

    public ServiceMethod(Object obj, MethodAccess access, int index) {
        this.object = obj;
        this.access = access;
        this.index = index;
    }

    public Object invoke(Object[] args) {
        return access.invoke(object, index, args);
    }

    public Class[] getParameterTypes() {
        return access.getParameterTypes()[index];
    }

    public Class getReturnType() {
        return access.getReturnTypes()[index];
    }
}
