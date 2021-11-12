package lark.task;

/**
 * @author cuigh
 */
@FunctionalInterface
public interface Executor {
    void execute(TaskContext ctx);
}
