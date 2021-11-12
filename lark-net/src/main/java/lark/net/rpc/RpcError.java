package lark.net.rpc;

import lark.core.lang.EnumValuable;

/**
 * @author cuigh
 */
public enum RpcError implements EnumValuable {
    // server 端异常, 1-50
    SERVER_UNKNOWN_ERROR(1, "未知错误"),
    SERVER_UNKNOWN_PROTOCOL(2, "无效的服务类型: %s - %s"),
    SERVER_SERVICE_NOT_FOUND(3, "服务未找到: %s.%s"),
    SERVER_BUSY(4, "服务器繁忙(业务处理线程池已满)"),
    SERVER_RETRY_OTHER_NODES(5, "请重试其它服务节点"),

    // client 端异常, 51-100
    CLIENT_UNKNOWN_ERROR(51, "未知错误: %s"),
    CLIENT_NO_PROVIDER(52, "找不到服务节点: %s"),
    CLIENT_UNKNOWN_PROTOCOL(53, "未知的服务协议: %s"),
    CLIENT_ACQUIRE_FAILED(54, "从连接池中获取连接失败: %s"),
    CLIENT_WRITE_FAILED(55, "发送请求到服务器失败: %s"),
    CLIENT_READ_FAILED(56, "读取服务器响应失败: %s"),
    CLIENT_CHANNEL_CLOSED(57, "连接已关闭"),
    CLIENT_DEADLINE_EXCEEDED(58, "请求超时");

    private int value;
    private String message;

    RpcError(int value, String message) {
        this.value = value;
        this.message = message;
    }

    @Override
    public int value() {
        return this.value;
    }

    public String message() {
        return this.message;
    }

    public String formatMessage(Object... args) {
        return String.format(this.message, args);
    }

    /**
     * 是否是 RPC 系统异常
     *
     * @param code
     * @return
     */
    public static boolean isRpcError(int code) {
        return code > 0 && code <= 100;
    }
}
