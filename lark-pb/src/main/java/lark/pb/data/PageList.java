package lark.pb.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lark.pb.annotation.ProtoField;
import lark.pb.field.FieldType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 分页列表
 */
@JsonSerialize(using = PageList.PageListSerializer.class)
public class PageList<T> implements Collection<T> {
    @ProtoField(order = 1, type = FieldType.INT32, required = true, description = "总记录数")
    private int totalCount;

    @ProtoField(order = 2, description = "当前页结果列表")
    private List<T> items;

    public PageList() {
        // default ctor
    }

    public PageList(List<T> items, int totalCount) {
        this.items = items;
        this.totalCount = totalCount;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        if (totalCount < 0) {
            throw new IllegalArgumentException("totalCount can't be negative.");
        }
        this.totalCount = totalCount;
    }

    public void addItem(T item) {
        ensureItems();
        items.add(item);
    }

    public void addItems(Collection<? extends T> c) {
        ensureItems();
        items.addAll(c);
    }

    @Override
    public int size() {
        return this.items == null ? 0 : this.items.size();
    }

    @Override
    public boolean isEmpty() {
        return this.items == null || this.items.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.items != null && this.items.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        ensureItems();
        return this.items.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.items == null ? new Object[0] : this.items.toArray();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E[] toArray(E[] a) {
        ensureItems();
        return this.items.toArray(a);
    }

    @Override
    public boolean add(T t) {
        ensureItems();
        return this.items.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return this.items != null && this.items.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.items != null && this.items.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        ensureItems();
        return this.items.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.items != null && this.items.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.items != null && this.items.retainAll(c);
    }

    @Override
    public void clear() {
        if (this.items != null) {
            this.items.clear();
        }
    }

    private void ensureItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
    }

    public static class PageListSerializer extends JsonSerializer<PageList<?>> {
        @Override
        public void serialize(PageList<?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeNumberField("totalCount", value.totalCount);
            if (value.items == null) {
                gen.writeNullField("items");
            } else {
                gen.writeArrayFieldStart("items");
                for (int i = 0; i < value.items.size(); i++) {
                    Object item = value.items.get(i);
                    gen.writeObject(item);
                }
                gen.writeEndArray();
            }
            gen.writeEndObject();
        }
    }
}
