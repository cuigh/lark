package lark.net.rpc.client.resolver;

import lark.net.Address;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author cuigh
 */
public interface AddressHolder {
    List<Address> get();

    int subscribe(Consumer<List<Address>> consumer);

    boolean unsubscribe(int id);
}

