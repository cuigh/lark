package lark.pb.coder;

/**
 * @author cuigh
 */
public interface CoderFactory {
    Coder get(Class<?> clazz);
}
