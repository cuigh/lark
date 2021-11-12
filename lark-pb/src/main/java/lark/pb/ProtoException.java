package lark.pb;

/**
 * Protobuf 异常
 *
 * @author cuigh
 */
public class ProtoException extends RuntimeException {
    public ProtoException() {
        super();
    }

    public ProtoException(String msg) {
        super(msg);
    }

    public ProtoException(Throwable inner) {
        super(inner);
    }

    public ProtoException(String msg, Throwable inner) {
        super(msg, inner);
    }
}
