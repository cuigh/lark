package lark.net.rpc.client;

import lark.core.context.Context;
import lark.net.rpc.RequestMessage;
import lark.net.rpc.ResponseMessage;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author cuigh
 */
public class Call {
    // TODO: 添加 AtomicBoolean released 变量防止多次 release;

    Call next;
    ClientChannel channel;
    Class<?> returnType;
    private RequestMessage request = new RequestMessage();
    private ResponseMessage response;
    private ReentrantLock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();

    public RequestMessage getRequest() {
        return request;
    }

    void reset(String server, String service, String method, Object[] args, Class<?> returnType) {
        response = null;
        request.reset(server, service, method, args);
        request.setTimeout(Context.get().getTimeout());
        this.returnType = returnType;
    }

    void finish(ResponseMessage response) {
        lock.lock();
        try {
            this.response = response;
            // signalAll
            cond.signal();
        } finally {
            lock.unlock();
        }
    }

    ResponseMessage get(Duration timeout) throws InterruptedException {
        return get(new Date(System.currentTimeMillis() + timeout.toMillis()));
    }

    ResponseMessage get(long timeout, TimeUnit unit) throws InterruptedException {
        return get(new Date(System.currentTimeMillis() + unit.toMillis(timeout)));
    }

    private ResponseMessage get(Date deadline) throws InterruptedException {
        lock.lock();
        try {
            while (this.response == null) {
                // false: 到达截止时间，true: 未到达截至时间
                boolean result = cond.awaitUntil(deadline);
                if (!result) {
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
        return this.response;
    }
}
