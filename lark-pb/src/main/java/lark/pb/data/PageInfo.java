package lark.pb.data;

import lark.pb.annotation.ProtoField;
import lark.pb.field.FieldType;

/**
 * 可用于 protocol buffer 序列化的通用分页信息对象。
 */
public class PageInfo {
    /**
     * 页码
     */
    @ProtoField(order = 1, type = FieldType.INT32, required = true, description = "页码，从 1 开始")
    private int pageIndex = 1;

    /**
     * 每页大小
     */
    @ProtoField(order = 2, type = FieldType.INT32, required = true, description = "每页大小")
    private int pageSize = 20;

    public PageInfo() {
        // default ctor
    }

    public PageInfo(int limit) {
        this(1, limit);
    }

    public PageInfo(int pageIndex, int pageSize) {
        if (pageIndex < 1) {
            throw new IllegalArgumentException("pageIndex must be greater than 0");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be greater than 0");
        }
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return this.pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
