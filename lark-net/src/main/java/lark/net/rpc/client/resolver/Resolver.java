package lark.net.rpc.client.resolver;

/**
 * @author cuigh
 */
public interface Resolver {
    AddressHolder resolve(String name, String version);
}
