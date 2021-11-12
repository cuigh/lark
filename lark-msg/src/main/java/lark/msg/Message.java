package lark.msg;

import java.time.Duration;
import java.util.Date;

/**
 * @author cuigh
 */
public interface Message {
    /**
     * 获取消息 ID
     *
     * @return
     */
    String getId();

    /**
     * 获取消息内容
     *
     * @return
     */
    String getBody();

    /**
     * 获取消息发送时间
     *
     * @return
     */
    Date getSendTime();

    /**
     * 获取消息接收时间
     *
     * @return
     */
    Date getReceiveTime();

    /**
     * 获取消息发送次数
     *
     * @return
     */
    int getAttempts();

    /**
     * 设置消息处理完成
     */
    void finish();

    /**
     * 把消息重新放入队列，并设置延迟
     *
     * @param delay 延迟时间
     */
    void requeue(Duration delay);

    /**
     * 把消息重新放入队列
     */
    void requeue();
}
