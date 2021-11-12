package lark.core.app.registry;

import lark.core.codec.JsonCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.zalando.boot.etcd.EtcdClient;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Supplier;

/**
 * @author cuigh
 */
public class EtcdRegistry implements Registry {
    private static final Logger LOGGER = LoggerFactory.getLogger(EtcdRegistry.class);
    private String name;
    private String id;
    private Supplier<Map<String, Object>> properties;
    private EtcdClient etcdClient;
    private TaskScheduler scheduler;
    private ScheduledFuture future;
    private Duration interval = Duration.ofSeconds(30);
    private int ttl = 35;

    public EtcdRegistry(EtcdClient etcdClient, TaskScheduler scheduler) {
        Objects.requireNonNull(etcdClient);
        Objects.requireNonNull(scheduler);
        this.etcdClient = etcdClient;
        this.scheduler = scheduler;
    }

    public void setInterval(Duration interval) {
        if (interval != null) {
            this.interval = interval;
            this.ttl = (int) interval.getSeconds() + 5;
        }
    }

    @Override
    public void setInfo(String name, String id, Supplier<Map<String, Object>> properties) {
        this.name = name;
        this.id = id;
        this.properties = properties;
    }

    @Override
    public synchronized void start() {
        if (this.future != null) {
            return;
        }

        this.future = scheduler.scheduleWithFixedDelay(() -> {
            try {
                String key = getPath(name, id);
                String value = JsonCodec.encode(properties.get());
                etcdClient.put(key, value, ttl);
                LOGGER.debug("Registered provider: {}", value);
            } catch (Exception e) {
                LOGGER.error("Failed to register provider", e);
            }
        }, interval);
    }

    @Override
    public synchronized void stop() {
        if (this.future == null) {
            return;
        }

        this.future.cancel(true);
        try {
            String key = getPath(name, id);
            etcdClient.delete(key);
            LOGGER.info("Removed provider from registry");
        } catch (Exception e) {
            LOGGER.error("Failed to remove provider", e);
        } finally {
            this.future = null;
        }
    }

    private String getPath(String name, String id) {
        return String.format("app/%s/providers/%s", name, id);
    }
}
