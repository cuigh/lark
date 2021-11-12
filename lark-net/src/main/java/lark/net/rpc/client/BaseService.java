package lark.net.rpc.client;

import java.util.Map;

/**
 * @author cuigh
 */
public abstract class BaseService {
    //    private static final Logger LOGGER = LoggerFactory.getLogger(BaseService.class);
    private Client client;
    private Map<String, MethodInfo> methods;

    protected BaseService(Client client, Class cls) {
        this.client = client;
        this.methods = MethodInfo.get(cls);
    }

    protected Object invoke(String method, Object[] args) {
        return client.invoke(methods.get(method), args);
//        LOGGER.info("Invoke: {}, {}, {}", method, args, returnType);
//        return null;
    }
}
