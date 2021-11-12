package lark.msg;

import lark.core.app.Application;
import org.apache.commons.logging.Log;
import org.springframework.core.io.ResourceLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author cuigh
 */
public class MsgApplication extends Application {
    private Subscriber consumer;
    private List<Subscription> subs = new ArrayList<>();

    public MsgApplication(Class<?>... primarySources) {
        this(null, primarySources);
    }

    public MsgApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
        super(resourceLoader, primarySources);
    }

    @Override
    protected void load() {
        super.load();
        this.consumer = ctx.getBean(Subscriber.class);
        subscribe();
    }

    @Override
    protected void start() {
        Log logger = getApplicationLog();
        subs.forEach(sub -> {
            try {
                consumer.subscribe(sub);
                logger.info(String.format("subscribe > ok, topic:%s, channel:%s, threads:%d, handler:%s",
                        sub.getTopic(), sub.getChannel(), sub.getThreads(), sub.getHandler().getClass()));
            } catch (Exception e) {
                logger.error(String.format("subscribe > failed, topic:%s, channel:%s, handler:%s",
                        sub.getTopic(), sub.getChannel(), sub.getHandler().getClass()), e);
            }
        });
    }

    private void subscribe() {
        Log logger = getApplicationLog();

        Map<String, Handler> beans = ctx.getBeansOfType(Handler.class);
        logger.info(String.format("Found %d handlers", beans.size()));

        beans.forEach((k, handler) -> {
            Class<?> clazz = handler.getClass();
            MsgHandler msgHandler = clazz.getAnnotation(MsgHandler.class);
            if (msgHandler == null) {
                logger.warn(String.format("Type [%s] isn't marked with @MsgHandler, skipping auto subscribe", clazz));
                return;
            }

            Subscription sub = new Subscription(msgHandler, handler);
//            ctx.getBean(msgHandler.provider()).subscribe(sub);
            subs.add(sub);
        });
    }
}
