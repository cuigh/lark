package lark.net.rpc.client;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author cuigh
 */
public class CallPool {
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Call free;
    private long counter;

    public Call acquire() {
        lock.writeLock().lock();
        long id = ++counter;
        try {
            Call call = this.free;
            if (call == null) {
                call = new Call();
            } else {
                this.free = call.next;
            }
            call.getRequest().setId(id);
            return call;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void release(Call call) {
        // TODO: 防止多次释放
        lock.writeLock().lock();
        try {
            call.next = this.free;
            this.free = call;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
