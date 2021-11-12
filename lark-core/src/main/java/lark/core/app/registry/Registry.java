package lark.core.app.registry;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 程序信息注册器
 *
 * @author cuigh
 */
public interface Registry {
    /**
     * Set application information
     *
     * @param name       application name
     * @param id         application id
     * @param properties application properties supplier
     */
    void setInfo(String name, String id, Supplier<Map<String, Object>> properties);

    /**
     * Start register
     */
    void start();

    /**
     * Stop register and remove register information from registry
     */
    void stop();
}
