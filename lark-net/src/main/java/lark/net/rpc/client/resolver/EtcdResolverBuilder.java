package lark.net.rpc.client.resolver;

import org.springframework.scheduling.TaskScheduler;
import org.zalando.boot.etcd.EtcdClient;

import java.time.Duration;
import java.util.Map;

/**
 * @author cuigh
 */
public class EtcdResolverBuilder implements ResolverBuilder {
    private EtcdClient etcdClient;
    private TaskScheduler scheduler;

    public EtcdResolverBuilder(EtcdClient etcdClient, TaskScheduler scheduler) {
        this.etcdClient = etcdClient;
        this.scheduler = scheduler;
    }

    @Override
    public String getName() {
        return "etcd";
    }

    @Override
    public Resolver build(Map<String, Object> options) {
        Duration interval = Duration.parse((String) options.get("refresh-interval"));
        EtcdResolver resolver = new EtcdResolver(etcdClient, scheduler);
        resolver.setRefreshInterval(interval);
        return resolver;
    }
}
