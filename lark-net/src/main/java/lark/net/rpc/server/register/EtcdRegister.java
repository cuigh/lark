package lark.net.rpc.server.register;

import lark.core.codec.JsonCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.zalando.boot.etcd.EtcdClient;
import org.zalando.boot.etcd.EtcdException;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * @author cuigh
 */
public class EtcdRegister implements Register {
    private static final Logger LOGGER = LoggerFactory.getLogger(EtcdRegister.class);
    private static final int NOT_FOUND = 100;
    private final TaskScheduler scheduler;
    private final EtcdClient etcdClient;
    private String serverAddress;
    private Duration ttl = Duration.ofSeconds(30);

    public EtcdRegister(EtcdClient etcdClient, TaskScheduler scheduler) {
        this.etcdClient = etcdClient;
        this.scheduler = scheduler;
    }

    private static String getPath(String name, String address) {
        return String.format("service/%s/providers/%s", name, address);
    }

    private static String getOfflinePath(Provider provider) {
        return String.format("service/%s/offlines/%s", provider.getName(), provider.getAddress());
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void setTtl(Duration ttl) {
        this.ttl = ttl;
    }

    @Override
    public void register(Supplier<Provider> supplier) {
        this.scheduler.scheduleWithFixedDelay(() -> {
            Provider provider = supplier.get();
            if (provider == null) {
                LOGGER.error("register failed: supplier return null");
            } else {
                if (provider.getAddress().startsWith(":")) {
                    provider.setAddress(serverAddress + provider.getAddress());
                }
                register(provider, this.ttl);
            }
        }, ttl);
    }

    @Override
    public void remove(Provider provider) {
        String key = getPath(provider.getName(), provider.getAddress());
        try {
            etcdClient.delete(key);
            LOGGER.info("remove provider [{} - {}] success", provider.getName(), provider.getAddress());
        } catch (EtcdException e) {
            LOGGER.error("remove provider [{} - {}] failed", provider.getName(), provider.getAddress(), e);
        }
    }

    private void register(Provider provider, Duration ttl) {
        // 检查节点是否下线
        try {
            String key = getOfflinePath(provider);
            etcdClient.get(key);
            LOGGER.info("register failed, node [{} - {}] is offline", provider.getName(), provider.getAddress());
            return;
        } catch (EtcdException e) {
            if (e.getError().getErrorCode() != NOT_FOUND) {
                LOGGER.error("register failed", e);
                return;
            }
        }

        // 注册
        try {
            String key = getPath(provider.getName(), provider.getAddress());
            String value = JsonCodec.encode(provider);
            etcdClient.put(key, value, (int) ttl.getSeconds() + 5);
            LOGGER.info("register success: {}", value);
        } catch (Exception e) {
            LOGGER.error("register failed", e);
        }
    }
}
