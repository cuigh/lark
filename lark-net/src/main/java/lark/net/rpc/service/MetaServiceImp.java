package lark.net.rpc.service;

import lark.net.rpc.server.Server;
import lark.net.rpc.server.ServiceInfo;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * @author cuigh
 */
public class MetaServiceImp implements MetaService {
    private Server server;

    public MetaServiceImp(Server server) {
        Objects.requireNonNull(server);
        this.server = server;
    }

    @Override
    public ServiceList getServiceList() {
        ServiceList list = new ServiceList();

        Map<String, ServiceInfo> services = server.getContainer().getServices();
        list.services = new ArrayList<>(services.size());
        services.forEach((k, v) -> list.services.add(new Service(v, false)));

        return list;
    }

    @Override
    public Service getService(String serviceName) {
        Map<String, ServiceInfo> services = server.getContainer().getServices();
        ServiceInfo si = services.get(serviceName);
        return si == null ? null : new Service(si, true);
    }

    @Override
    public Method getMethod(String serviceName, String methodName) {
        Map<String, ServiceInfo> services = server.getContainer().getServices();
        ServiceInfo si = services.get(serviceName);
        if (si == null) {
            return null;
        }
        ServiceInfo.MethodInfo mi = si.getMethods().get(methodName);
        return mi == null ? null : new Method(mi);
    }

    @Override
    public Type getType(String typeID) {
        try {
            Class<?> clazz = Class.forName(typeID);
            return new Type(clazz, true);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
