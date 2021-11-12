package lark.net.rpc.server;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lark.core.sync.ThreadPoolBuilder;
import lark.net.rpc.RequestMessage;
import lark.net.rpc.RpcError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * @author cuigh
 */
public class InvokeDispatcher implements InvokeHandler, RejectedExecutionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvokeDispatcher.class);
    private final ThreadPoolExecutor threadPool;
    private List<ServerFilter> filters = new ArrayList<>();
    private InvokeHandler handler = this;
    private JobPool jobPool = new JobPool(this::createJob);

    /**
     * 构造函数
     *
     * @param min     core pool size
     * @param max     max pool size
     * @param backlog job queue size
     */
    public InvokeDispatcher(int min, int max, int backlog) {
        this.threadPool = new ThreadPoolBuilder("RpcServerBiz-", min, max, backlog).setRejection(this).build();
    }

    @Override
    public Object handle(InvokeContext ctx) {
        Job job = (Job) ctx;
        return job.method.invoke(job.msg.getArgs());
    }

    void use(ServerFilter... filters) {
        Objects.requireNonNull(filters);
        Collections.addAll(this.filters, filters);
        this.handler = this;
        for (ServerFilter filter : this.filters) {
            this.handler = filter.apply(this.handler);
        }
    }

    private Job createJob() {
        return new Job(jobPool, handler);
    }

    public int getActiveCount() {
        return threadPool.getActiveCount();
    }

    public void offer(Channel channel, ServiceMethod method, RequestMessage msg) {
        Job job = jobPool.acquire();
        job.reset(channel, method, msg);
        threadPool.execute(job);
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        Job job = (Job) r;
        try {
            InvokeResult result = InvokeResult.failure(job.msg.getId(), RpcError.SERVER_BUSY);
            job.channel.writeAndFlush(result);
            LOGGER.error("Server is too busy(min={}, max={}, backlog={})",
                    executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getMaximumPoolSize());
        } finally {
            jobPool.release(job);
        }
    }

    static class Job implements Runnable, InvokeContext {
        private static final AttributeKey<Object> USER_ATTRIBUTE_KEY = AttributeKey.newInstance("user");
        private Channel channel;
        private ServiceMethod method;
        private RequestMessage msg;
        private long deadline;
        private JobPool pool;
        private InvokeHandler handler;
        private Job next;

        Job(JobPool pool, InvokeHandler handler) {
            this.pool = pool;
            this.handler = handler;
        }

        @Override
        public void run() {
            InvokeResult result;
            try {
                result = InvokeResult.success(msg.getId(), handler.handle(this));
            } catch (Exception e) {
                LOGGER.error("Failed to handle request", e);
                result = InvokeResult.failure(msg.getId(), e);
            } finally {
                pool.release(this);
            }

            if (deadline == 0 || deadline > System.currentTimeMillis()) {
                channel.writeAndFlush(result);
            } else {
                LOGGER.warn("丢弃超时请求({}ms): {}{}", msg.getTimeout(), msg.getService(), msg.getMethod());
            }
        }

        @Override
        public RequestMessage getRequest() {
            return msg;
        }

        @Override
        public Object getUser() {
            return channel.attr(USER_ATTRIBUTE_KEY).get();
        }

        @Override
        public void setUser(Object user) {
            channel.attr(USER_ATTRIBUTE_KEY).set(user);
        }

        void reset(Channel channel, ServiceMethod method, RequestMessage msg) {
            this.channel = channel;
            this.method = method;
            this.msg = msg;
            this.deadline = (msg.getTimeout() == 0) ? 0 : (System.currentTimeMillis() + msg.getTimeout());
        }
    }

    private class JobPool {
        private ReentrantLock lock = new ReentrantLock();
        private Job job;
        private Supplier<Job> supplier;

        private JobPool(Supplier<Job> supplier) {
            this.supplier = supplier;
        }

        Job acquire() {
            lock.lock();
            try {
                Job j = job;
                if (j != null) {
                    this.job = j.next;
                    return j;
                }
                return supplier.get();
            } finally {
                lock.unlock();
            }
        }

        void release(Job job) {
            lock.lock();
            try {
                job.next = this.job;
                this.job = job;
            } finally {
                lock.unlock();
            }
        }
    }
}
