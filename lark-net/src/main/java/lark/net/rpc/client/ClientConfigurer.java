package lark.net.rpc.client;

import lark.net.rpc.client.resolver.ResolverBuilder;

import java.util.List;

/**
 * @author cuigh
 */
public interface ClientConfigurer {
    default void addResolvers(List<ResolverBuilder> builders) {
    }

    default void addFilters(List<ClientFilter> filters) {
    }
}
