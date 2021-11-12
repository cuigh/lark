package lark.core.sync;

import lark.core.lang.Linkable;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * Simple object pool.
 *
 * @param <T> object type
 * @author cuigh
 */
public class Pool<T extends Linkable<T>> {
    /**
     * 对象池最大容量(不完全精确)
     */
    private final int maxSize;
    private T queue;
    private Supplier<T> supplier;
    private Lock lock = new ReentrantLock();
    private int size;

    public Pool(Supplier<T> supplier) {
        this(supplier, Integer.MAX_VALUE);
    }

    public Pool(Supplier<T> supplier, int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException();
        }
        this.supplier = supplier;
        this.maxSize = maxSize;
    }

    public T acquire() {
        lock.lock();
        try {
            if (queue == null) {
                return supplier.get();
            }

            T item = queue;
            queue = queue.getNext();
            size--;
            return item;
        } finally {
            lock.unlock();
        }
    }

    public void release(T item) {
        lock.lock();
        try {
            if (size < maxSize) {
                item.setNext(queue);
                queue = item;
                size++;
            }
        } finally {
            lock.unlock();
        }
    }
}
