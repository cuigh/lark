package lark.net.rpc.client.resolver;

import lark.core.codec.JsonCodec;
import lark.net.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.zalando.boot.etcd.EtcdClient;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author cuigh
 */
public abstract class BaseResolver implements Resolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseResolver.class);
    private final TaskScheduler scheduler;
    private Duration refreshInterval = Duration.ofSeconds(5);
    private Map<String, AddressHolder> holders = new ConcurrentHashMap<>();

    public BaseResolver(TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public AddressHolder resolve(String name, String version) {
        return holders.computeIfAbsent(name, n -> {
            AddressHolderImpl holder = new AddressHolderImpl(this::discover, name, version);
            holder.future = scheduler.scheduleWithFixedDelay(holder, Instant.now().plus(refreshInterval), refreshInterval);
            return holder;
        });
    }

    public void setRefreshInterval(Duration refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    protected abstract Set<Address> discover(String name, String version);

    private static class AddressHolderImpl implements AddressHolder, Runnable {
        private BiFunction<String, String, Set<Address>> discoverer;
        private String name;
        private String version;
        private Map<Integer, Consumer<List<Address>>> consumers = new ConcurrentHashMap<>();
        private List<Address> addresses;
        private ScheduledFuture future;
        private AtomicInteger counter = new AtomicInteger();

        AddressHolderImpl(BiFunction<String, String, Set<Address>> discoverer, String name, String version) {
            this.discoverer = discoverer;
            this.name = name;
            this.version = version;
            this.addresses = new ArrayList<>(discoverer.apply(name, version));
        }

        private Address createAddress(Map<String, Object> options) {
            Address address = new Address();
            address.setUrl((String) options.get("address"));
            return address;
        }

        public void stop() {
            future.cancel(true);
        }

        @Override
        public void run() {
            try {
                Set<Address> set = discoverer.apply(name, version);
                if (set.isEmpty()) {
                    LOGGER.warn("No addresses available for service [{}]", name);
                    return;
                }

                if (set.size() == addresses.size() && set.containsAll(addresses)) {
                    return;
                }

                LOGGER.info("Addresses of [{}] refreshed: {}", name, JsonCodec.encode(set));
                this.addresses = new ArrayList<>(set);
                consumers.forEach((id, c) -> c.accept(addresses));
            } catch (Exception e) {
                LOGGER.error("Failed to refresh addresses of [" + name + "]", e);
            }
        }

        @Override
        public List<Address> get() {
            return addresses;
        }

        @Override
        public int subscribe(Consumer<List<Address>> consumer) {
            int id = counter.incrementAndGet();
            consumers.put(id, consumer);
            return id;
        }

        @Override
        public boolean unsubscribe(int id) {
            return consumers.remove(id) != null;
        }
    }
}
