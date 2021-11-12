package lark.msg;

/**
 * @author cuigh
 */
@FunctionalInterface
public interface Handler {
    void handle(Message message);
}
