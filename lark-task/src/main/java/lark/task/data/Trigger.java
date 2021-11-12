package lark.task.data;

import lark.pb.annotation.ProtoField;
import lark.pb.annotation.ProtoMessage;

import java.time.LocalDateTime;

/**
 * @author cuigh
 */
@ProtoMessage(description = "触发器")
public class Trigger {
    @ProtoField(order = 1, required = true, description = "cron 表达式")
    private String cron;

    @ProtoField(order = 2, description = "调度起始时间")
    private LocalDateTime start;

    @ProtoField(order = 3, description = "调度结束时间")
    private LocalDateTime end;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
}