package lark.core.app.registry;

import java.util.Map;

/**
 * @author cuigh
 */
public interface Collector {
    /**
     * Collect application information
     *
     * @param properties information container
     */
    void collect(Map<String, Object> properties);
}
