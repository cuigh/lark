package lark.msg;

/**
 * @author cuigh
 */
public class Subscription {
    private String topic;
    private String channel;
    private int threads;
    private Handler handler;

    public Subscription() {
    }

    public Subscription(MsgHandler mh, Handler handler) {
        this.topic = mh.topic();
        this.channel = mh.channel();
        this.threads = mh.threads();
        this.handler = handler;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}
