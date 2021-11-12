package lark.core.lang;

/**
 * Optional 类是不可修改的, 而 Java 的 Lambda 表达式要求对象对象必须为 final, 当需要修改对象时就会很麻烦, 此类主要用来处理此类问题
 *
 * @author cuigh
 */
public class Nullable<T> {
    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public boolean hasValue() {
        return this.value != null;
    }
}
