package lark.net.rpc.server;

import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Random;

/**
 * @author cuigh
 */
public class ServerOptions {
    private String name;
    private String type = "simple";
    private String version;
    private String group;
    private boolean register;
    private String address;
    private String description;
    private int maxClients = 1000;
    private int backlog;
    private int acceptThreads = Runtime.getRuntime().availableProcessors();
    private int workThreads = Runtime.getRuntime().availableProcessors() * 2;
    private int minProcessThreads = Runtime.getRuntime().availableProcessors() * 2;
    private int maxProcessThreads = 200;
    private Duration dialTimeout = Duration.ofSeconds(10);
    private Duration readTimeout = Duration.ofSeconds(30);
    private Duration writeTimeout = Duration.ofSeconds(30);
    private Duration idleTime = Duration.ofMinutes(30);
    private boolean keepAlive;
    private boolean tcpNoDelay;
    private int linger = 5;
    private int receiveBufferSize = 1024 * 64;
    private int sendBufferSize = 1024 * 64;

    public ServerOptions() {
//        this.settings = new SettingMap();
//        this.register = AppConfig.getDefault().getGlobal().isRpcRegisterEnabled();
    }

    public ServerOptions(String address, String description) {
//        this.settings = new SettingMap();
        this.setAddress(address);
        this.description = description;
//        this.name = AppConfig.getDefault().getAppName();
//        this.register = AppConfig.getDefault().getGlobal().isRpcRegisterEnabled();
    }

    public Duration getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(Duration idleTime) {
        this.idleTime = idleTime;
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public int getLinger() {
        return linger;
    }

    public void setLinger(int linger) {
        this.linger = linger;
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public int getMinProcessThreads() {
        return minProcessThreads;
    }

    public void setMinProcessThreads(int minProcessThreads) {
        this.minProcessThreads = minProcessThreads;
    }

    public int getMaxProcessThreads() {
        return maxProcessThreads;
    }

    public void setMaxProcessThreads(int maxProcessThreads) {
        this.maxProcessThreads = maxProcessThreads;
    }

    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
    }

    public int getSendBufferSize() {
        return sendBufferSize;
    }

    public void setSendBufferSize(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
    }

    public int getAcceptThreads() {
        return acceptThreads;
    }

    public void setAcceptThreads(int acceptThreads) {
        this.acceptThreads = acceptThreads;
    }

    public int getWorkThreads() {
        return workThreads;
    }

    public void setWorkThreads(int workThreads) {
        this.workThreads = workThreads;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public int getMaxClients() {
        return maxClients;
    }

    public void setMaxClients(int maxClients) {
        this.maxClients = maxClients;
    }

    public Duration getDialTimeout() {
        return dialTimeout;
    }

    public void setDialTimeout(Duration dialTimeout) {
        this.dialTimeout = dialTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Duration getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(Duration writeTimeout) {
        this.writeTimeout = writeTimeout;
    }
//    private SettingMap settings;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void cover(ServerOptions options) {
        if (options == null) {
            return;
        }

        if (!StringUtils.isEmpty(options.name)) {
            this.name = options.name;
        }
        if (!StringUtils.isEmpty(options.type)) {
            this.type = options.type;
        }
        if (!StringUtils.isEmpty(options.version)) {
            this.version = options.version;
        }
        this.register = options.register;
        if (!StringUtils.isEmpty(options.address)) {
            this.setAddress(options.address);
        }
        if (!StringUtils.isEmpty(options.description)) {
            this.description = options.description;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isRegister() {
        return register;
    }

    public void setRegister(boolean register) {
        this.register = register;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        int port = 0;
        String[] parts = address.split(":");
        if (parts.length > 1 && !parts[1].isEmpty()) {
            port = Integer.parseInt(parts[1]);
        }

        if (port <= 0) {
            // 如果没有设置端口，则使用 [20000,30000) 内的随机端口
            port = new Random().nextInt(10000) + 20000;
            this.address = ":" + Integer.toString(port);
            if (parts.length > 0) {
                this.address = parts[0] + this.address;
            }
        } else {
            this.address = address;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
