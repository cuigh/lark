package lark.core.lang;

/**
 * 支持对象链接
 *
 * @author cuigh
 */
public interface Linkable<T> {
    /**
     * Acquire next object.
     *
     * @return next object
     */
    T getNext();

    /**
     * Set next object.
     *
     * @param next the next object of current.
     */
    void setNext(T next);
}
