package lark.net.rpc.server.register;

import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author cuigh
 */
public final class Parameter {
    private String name;
    private String realName;
    private String group;
    private String version;
    private Consumer<List<Provider>> consumer;

    public Parameter(String name, String alias) {
        this.name = name;
        this.realName = StringUtils.isEmpty(alias) ? name : alias;
    }

    public String getName() {
        return name;
    }

    public String getRealName() {
        return realName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Consumer<List<Provider>> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<List<Provider>> consumer) {
        this.consumer = consumer;
    }
}
