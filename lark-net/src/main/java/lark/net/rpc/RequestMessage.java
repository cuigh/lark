package lark.net.rpc;

import java.util.Map;

/**
 * @author cuigh
 */
public class RequestMessage {
    /**
     * 消息 ID, 每个请求唯一
     */
    private long id;
    private String client;
    private String server;
    private String service;
    private String method;
    private Object[] args;
    /**
     * 超时时间(ms)
     */
    private long timeout;
    private Map<String, String> labels;

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public void reset(String server, String service, String method, Object[] args) {
        this.server = server;
        this.service = service;
        this.method = method;
        this.args = args;
        if (this.labels != null) {
            this.labels.clear();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
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

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
