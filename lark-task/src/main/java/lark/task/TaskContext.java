package lark.task;

import lark.task.data.Arg;
import lark.task.data.ExecuteParam;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cuigh
 */
public class TaskContext {
    private String name;
    private String id;
    private String alias;
    private Map<String, String> args = new HashMap<>();

    public TaskContext(ExecuteParam param) {
        this.name = param.getName();
        this.id = param.getId();
        this.alias = param.getAlias();
        List<Arg> list = param.getArgs();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(arg -> this.args.put(arg.getName(), arg.getValue()));
        }
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }

    public Map<String, String> getArgs() {
        return args;
    }
}
