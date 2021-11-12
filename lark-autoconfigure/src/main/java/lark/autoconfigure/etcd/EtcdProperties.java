package lark.autoconfigure.etcd;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for etcd.
 *
 * @author cuigh
 */
@ConfigurationProperties(prefix = "etcd")
public class EtcdProperties {
    /**
     * indicates whether the etcd client is enabled
     */
    private boolean enabled = true;
    /**
     * etcd address.
     */
    private String address = "http://localhost:2379";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
