package lark.msg;

import java.time.Duration;
import java.util.List;

/**
 * @author cuigh
 */
public interface Publisher {
    /**
     * 发送消息
     *
     * @param topic 消息主题
     * @param msg 消息
     */
    void publish(String topic, Object msg);

    /**
     * 发送延迟消息，消息不会立即发送给订阅方，而是在指定时间后才发送
     *
     * @param topic 消息主题
     * @param msg 消息
     * @param delay 延迟时间, 注意延迟最大值为服务器端设置的 max-req-timeout(默认 60 分钟)
     */
    void publish(String topic, Object msg, Duration delay);

    /**
     * 批量发送消息
     *
     * @param topic 消息主题
     * @param msgs 消息列表
     * @param <T>
     */
    <T> void publishBatch(String topic, List<T> msgs);
}
