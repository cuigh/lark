package lark.net.rpc.server.register;

import java.util.function.Supplier;

/**
 * @author cuigh
 */
public interface Register {
    void register(Supplier<Provider> supplier);

    void remove(Provider provider);
}
