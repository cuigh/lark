package lark.core.sync;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * @author cuigh
 */
public class Lazy<T> {
    private ReentrantLock lock = new ReentrantLock();
    private T value;
    private Supplier<T> supplier;

    public Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T getValue() {
        if (this.value == null) {
            lock.lock();
            try {
                if (this.value == null) {
                    this.value = supplier.get();
                }
            } finally {
                lock.unlock();
            }
        }
        return value;
    }
}
