package lark.core.app.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author cuigh
 */
public class Provider {
    private String id;
    private Map<String, Object> properties = new HashMap<>();

    public Provider() {
    }

    public Provider(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public void setProperty(String key, Object value) {
        this.properties.put(key, value);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Provider) {
            return Objects.equals(this.id, ((Provider) obj).id);
        }
        return false;
    }
}
