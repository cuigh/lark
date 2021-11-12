package lark.net.rpc.server;

import lark.net.rpc.RpcError;
import lark.net.rpc.RpcException;

/**
 * @author cuigh
 */
public class InvokeResult {
    private long id;
    private Object value;
    private Exception error;

    public static InvokeResult failure(long id, Exception error) {
        InvokeResult result = new InvokeResult();
        result.id = id;
        result.error = error;
        return result;
    }

    public static InvokeResult failure(long id, RpcError error) {
        InvokeResult result = new InvokeResult();
        result.id = id;
        result.error = new RpcException(error);
        return result;
    }

    public static InvokeResult success(long id, Object value) {
        InvokeResult result = new InvokeResult();
        result.id = id;
        result.value = value;
        return result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Exception getError() {
        return error;
    }

    public void setError(Exception error) {
        this.error = error;
    }
}
