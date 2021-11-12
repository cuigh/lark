package lark.msg;

/**
 * @author cuigh
 */
public interface Subscriber {
    /**
     * 订阅消息
     *
     * @param sub 订阅信息
     */
    void subscribe(Subscription sub);
}
