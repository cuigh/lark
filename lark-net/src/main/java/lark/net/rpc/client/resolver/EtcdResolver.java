package lark.net.rpc.client.resolver;

import lark.core.codec.JsonCodec;
import lark.net.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.CollectionUtils;
import org.zalando.boot.etcd.EtcdClient;
import org.zalando.boot.etcd.EtcdException;
import org.zalando.boot.etcd.EtcdNode;

import java.util.*;

/**
 * @author cuigh
 */
public class EtcdResolver extends BaseResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(EtcdResolver.class);
    private final EtcdClient etcdClient;

    public EtcdResolver(EtcdClient etcdClient, TaskScheduler scheduler) {
        super(scheduler);
        this.etcdClient = etcdClient;
    }

    @Override
    protected Set<Address> discover(String name, String version) {
        try {
            String dir = getPath(name);
            List<EtcdNode> nodes = etcdClient.get(dir).getNode().getNodes();
            if (!CollectionUtils.isEmpty(nodes)) {
                Set<Address> set = new HashSet<>();
                for (EtcdNode n : nodes) {
                    Map<String, Object> options = JsonCodec.decodeMap(n.getValue());
                    if (isValid(options, version)) {
                        set.add(createAddress(options));
                    }
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Found {} addresses for [name:{}, version:{}]: {}",
                            set.size(), name, version, JsonCodec.encode(set));
                }
                return set;
            }
        } catch (EtcdException e) {
            LOGGER.warn("Failed to discover addresses for [{}]", name, e);
        }
        return Collections.emptySet();
    }

    @SuppressWarnings("unchecked")
    private Address createAddress(Map<String, Object> options) {
        Map<String, Object> rpc = (Map<String, Object>) options.get("rpc");
        Address address = new Address();
        address.setUrl((String) rpc.get("address"));
        return address;
    }

    private boolean isValid(Map<String, Object> options, String version) {
        return version == null || version.equals(options.get("version"));
    }

    private String getPath(String name) {
        return String.format("app/%s/providers", name);
    }
}
