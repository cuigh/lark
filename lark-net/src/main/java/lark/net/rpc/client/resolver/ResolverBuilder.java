package lark.net.rpc.client.resolver;

import java.util.Map;

/**
 * @author cuigh
 */
public interface ResolverBuilder {
    String getName();

    Resolver build(Map<String, Object> options);
}
