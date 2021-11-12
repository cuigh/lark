package lark.net.rpc.server;

import lark.net.rpc.protocol.ServerProtocol;

import java.util.List;

/**
 * @author cuigh
 */
public interface ServerConfigurer {
    default void addProtocols(List<ServerProtocol> protocols) {
    }

    default void addFilters(List<ServerFilter> filters) {
    }
}
