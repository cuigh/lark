package lark.net.rpc;

import lark.core.app.registry.Collector;
import lark.core.util.Strings;
import lark.net.rpc.server.Server;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cuigh
 */
public class RpcCollector implements Collector {
    private final Server server;
    private String serverAddress;
    private String rpcAddress;

    public RpcCollector(final Server server) {
        this.server = server;
        this.rpcAddress = server.getOptions().getAddress();
    }

    public void setServerAddress(String serverAddress) {
        if (!Strings.isEmpty(serverAddress)) {
            this.serverAddress = serverAddress;
            if (server.getOptions().getAddress().startsWith(":")) {
                this.rpcAddress = serverAddress + server.getOptions().getAddress();
            }
        }
    }

    @Override
    public void collect(Map<String, Object> properties) {
        Map<String, Object> options = new HashMap<>(3);
        options.put("address", server.getOptions().getType() + "://" + rpcAddress);
        options.put("clients", server.getChannels().size());
        properties.put("rpc", options);
    }
}
