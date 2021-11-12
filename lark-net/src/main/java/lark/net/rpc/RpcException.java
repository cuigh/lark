package lark.net.rpc;

import lark.core.lang.BusinessException;

/**
 * @author cuigh
 */
public class RpcException extends BusinessException {
    public RpcException(RpcError error, Object... args) {
        super(error.value(), error.formatMessage(args));
    }

    public RpcException(RpcError error, Throwable e) {
        super(error.value(), error.formatMessage(e.getMessage()), e);
    }

    public RpcException(String message) {
        this(0, message);
    }

    public RpcException(int code, String message) {
        super(code, message);
    }

    public RpcException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
