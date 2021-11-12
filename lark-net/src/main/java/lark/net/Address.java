package lark.net;

import java.util.Map;
import java.util.Objects;

/**
 * @author cuigh
 */
public class Address {
    private String url;
    private Map<String, Object> options;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    @Override
    public int hashCode() {
        return this.url.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Address) {
            return Objects.equals(this.url, ((Address) obj).url);
        }
        return false;
    }
}
