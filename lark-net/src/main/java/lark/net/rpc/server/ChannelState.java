package lark.net.rpc.server;

import io.netty.util.AttributeKey;

import java.util.Date;

/**
 * @author cuigh
 */
public class ChannelState {
    public static final AttributeKey<ChannelState> KEY = AttributeKey.newInstance("ChannelState");

    private final Date createTime = new Date();
    private Date activeTime;
    private String client = "";
    private String service;
    private String method;

    public ChannelState() {
        activeTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(Date activeTime) {
        this.activeTime = activeTime;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        if (client != null) {
            this.client = client;
        }
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void refresh(String client, String service, String method) {
        this.activeTime = new Date();
        this.client = client;
        this.service = service;
        this.method = method;
    }

}
