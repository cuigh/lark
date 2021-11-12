package lark.net.rpc.client.balancer;

import lark.net.rpc.client.Node;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * @author cuigh
 */
public class BalancerFactory {
    private static final Map<String, Supplier<Balancer>> BUILDERS = new ConcurrentHashMap<>();

    static {
        register("random", RandomBalancer::new);
        register("round-robin", RoundRobinBalancer::new);
    }

    private BalancerFactory() {
        // 防止实例化
    }

    public static void register(String name, Supplier<Balancer> builder) {
        if (BUILDERS.putIfAbsent(name, builder) != null) {
            throw new RuntimeException("Balancer already exist: " + name);
        }
    }

    /**
     * 获取
     *
     * @param name 名称
     * @return
     */
    public static Balancer create(String name) {
        return BUILDERS.get(StringUtils.isEmpty(name) ? "random" : name).get();
    }

    private static class RandomBalancer implements Balancer {
        private final Random random = new Random();
        private List<Node> nodes;

        @Override
        public String name() {
            return "random";
        }

        @Override
        public List<Node> getNodes() {
            return nodes;
        }

        @Override
        public void setNodes(List<Node> nodes) {
            this.nodes = nodes;
        }

        @Override
        public Node select() {
            int index = random.nextInt(nodes.size());
            return nodes.get(index);
        }
    }

    private static class RoundRobinBalancer implements Balancer {
        private AtomicInteger counter = new AtomicInteger();
        private List<Node> nodes;

        @Override
        public String name() {
            return "round-robin";
        }

        @Override
        public List<Node> getNodes() {
            return nodes;
        }

        @Override
        public void setNodes(List<Node> nodes) {
            this.nodes = nodes;
        }

        @Override
        public Node select() {
            int index = counter.getAndIncrement() % nodes.size();
            return nodes.get(index);
        }
    }
}
