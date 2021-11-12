package lark.core.sync;

import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author cuigh
 */
public class ThreadPoolBuilder {
    private String threadName;
    private int coreSize = 1;
    private int maxSize = Integer.MAX_VALUE;
    private int queueSize = Integer.MAX_VALUE;
    private Duration idleTime = Duration.ofMinutes(1);
    private RejectedExecutionHandler rejection;
    private boolean allowCoreThreadExit = false;
    private Thread.UncaughtExceptionHandler exceptionHandler;

    /**
     * 构造函数
     *
     * @param threadName 线程名称前缀
     */
    public ThreadPoolBuilder(String threadName) {
        if (StringUtils.isEmpty(threadName)) {
            throw new IllegalArgumentException();
        }
        this.threadName = threadName;
    }

    /**
     * 构造函数
     *
     * @param threadName 线程名称前缀
     * @param coreSize   核心线程数
     * @param maxSize    最大线程数
     * @param queueSize  队列容量
     */
    public ThreadPoolBuilder(String threadName, int coreSize, int maxSize, int queueSize) {
        if (StringUtils.isEmpty(threadName)) {
            throw new IllegalArgumentException();
        }
        this.threadName = threadName;
        this.coreSize = coreSize;
        this.maxSize = maxSize;
        this.queueSize = queueSize;
    }

    public void setExceptionHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public ThreadPoolBuilder setCoreSize(int coreSize) {
        this.coreSize = coreSize;
        return this;
    }

    public ThreadPoolBuilder setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public ThreadPoolBuilder setQueueSize(int queueSize) {
        this.queueSize = queueSize;
        return this;
    }

    public ThreadPoolBuilder setIdleTime(Duration idleTime) {
        this.idleTime = idleTime;
        return this;
    }

    public ThreadPoolBuilder setRejection(RejectedExecutionHandler rejection) {
        this.rejection = rejection;
        return this;
    }

    public ThreadPoolBuilder setAllowCoreThreadExit(boolean allowCoreThreadExit) {
        this.allowCoreThreadExit = allowCoreThreadExit;
        return this;
    }

    public ThreadPoolExecutor build() {
        BlockingQueue<Runnable> queue = this.queueSize > 0 ? new LinkedBlockingQueue<>(queueSize) : new SynchronousQueue<>();
        ThreadFactory factory = createThreadFactory();
        ThreadPoolExecutor pool;
        if (this.rejection == null) {
            pool = new ThreadPoolExecutor(this.coreSize, this.maxSize, idleTime.getSeconds(), TimeUnit.SECONDS, queue, factory);
        } else {
            pool = new ThreadPoolExecutor(this.coreSize, this.maxSize, idleTime.getSeconds(), TimeUnit.SECONDS, queue, factory, rejection);
        }
        pool.allowCoreThreadTimeOut(this.allowCoreThreadExit);
        return pool;
    }

    private ThreadFactory createThreadFactory() {
        final ThreadFactory backingThreadFactory = Executors.defaultThreadFactory();
        final AtomicLong count = new AtomicLong(0);
        return runnable -> {
            Thread thread = backingThreadFactory.newThread(runnable);
            thread.setName(threadName + "-" + Long.toString(count.getAndIncrement()));
            if (exceptionHandler != null) {
                thread.setUncaughtExceptionHandler(exceptionHandler);
            }
            return thread;
        };
    }
}
