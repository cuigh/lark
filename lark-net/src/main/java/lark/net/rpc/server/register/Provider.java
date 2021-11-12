package lark.net.rpc.server.register;

import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author cuigh
 */
public class Provider {
    private String name;
    private String type;
    private String address;
    private String version;
    private String group;
    private String note;
    private String machine;
    private Map<String, String> settings;
    private int clients;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }

    public int getClients() {
        return clients;
    }

    public void setClients(int clients) {
        this.clients = clients;
    }

    public boolean isMatch(String version, String group) {
        if (!StringUtils.isEmpty(version) && !version.equals(this.version)) {
            return false;
        }
        return StringUtils.isEmpty(group) || group.equals(this.group);
    }
}
