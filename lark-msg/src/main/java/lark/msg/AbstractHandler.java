package lark.msg;

import lark.core.codec.JsonCodec;
import lark.core.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;

import java.time.Duration;

/**
 * 消息处理器抽象基类
 *
 * @param <T> 消息对象类型
 * @author cuigh
 */
public abstract class AbstractHandler<T> implements Handler {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractHandler.class);
    private static final Duration MAX_DELAY = Duration.ofMinutes(10);
    protected int maxAttempts = 3;
    protected boolean autoResponse = true;
    protected Class<T> msgClass;

    @SuppressWarnings("unchecked")
    public AbstractHandler() {
        ResolvableType resolvableType = ResolvableType.forClass(this.getClass());
        this.msgClass = (Class<T>) (resolvableType.getSuperType().getGeneric(0).resolve());
    }

    /**
     * 获取重发消息的延迟(单位毫秒)
     *
     * @param attempts retry count
     * @return resend delay time(ms)
     */
    protected Duration getDelay(int attempts) {
        Duration delay = Duration.ofMinutes(attempts);
        return delay.compareTo(MAX_DELAY) > 0 ? MAX_DELAY : delay;
    }

    @Override
    public void handle(Message m) {
        //订阅者接收时间戳
        String body = m.getBody();
        try {
            T msg = decodeMessage(body);
            this.process(msg, m);
            if (autoResponse) {
                m.finish();
            }
            logger.info("消息处理成功, ID: {}, 内容: {}, 接收时间: {}", m.getId(), body, m.getReceiveTime());
        } catch (Exception e) {
            if (!autoResponse) {
                logger.error("消息第 {} 次处理失败, ID: {}, 内容: {}, 错误:", m.getAttempts(), m.getId(), body, e);
                return;
            }

            if (m.getAttempts() < maxAttempts) {
                Duration delay = getDelay(m.getAttempts());
                logger.error("第 {} 次处理失败, 延迟 {} 重发消息, ID: {}, 内容: {}, 错误:",
                        m.getAttempts(), delay, m.getId(), body, e);
                m.requeue(delay);
            } else {
                logger.error("第 {} 次处理失败, 达到最大处理次数 {}, 丢弃消息, ID: {}, 内容: {}, 错误:",
                        m.getAttempts(), maxAttempts, m.getId(), body, e);
                m.finish();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private T decodeMessage(String body) {
        T msg;
        try {
            msg = JsonCodec.decode(body, msgClass);
        } catch (Exception e) {
            if (msgClass == String.class) {
                msg = (T) body;
            } else {
                throw Exceptions.asRuntime(e);
            }
        }
        return msg;
    }

    /**
     * 处理消息
     *
     * @param msg 消息对象
     * @param raw 原始消息
     */
    protected abstract void process(T msg, Message raw);
}
