package lark.pb.field;

import lark.pb.ProtoException;
import lark.pb.annotation.ProtoField;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 字段信息
 *
 * @author cuigh
 */
public class FieldInfo {
    private Field field;
    private FieldType type;
    private FieldKind kind;
    private int order;
    private boolean required;
    private String description;
    private ListInfo list;
    private MapInfo map;

    public FieldInfo(Class<?> declareType, Field field) {
        this.field = field;

        Class<?> cls = field.getType();
        if (List.class.isAssignableFrom(cls)) {
            kind = FieldKind.LIST;
            processList(declareType, field);
        } else if (Map.class.isAssignableFrom(cls)) {
            kind = FieldKind.MAP;
            processMap(declareType, field);
        }

        process(field);
    }

    public FieldKind getKind() {
        return kind;
    }

    public ListInfo getList() {
        return list;
    }

    public MapInfo getMap() {
        return map;
    }

    private void process(Field field) {
        ProtoField annotation = field.getAnnotation(ProtoField.class);
        this.type = annotation.type();
        if (this.type == FieldType.AUTO) {
            if (this.kind == FieldKind.MAP) {
                this.type = FieldType.MESSAGE;
            } else {
                this.type = FieldUtils.getFieldType(this.kind == FieldKind.LIST ? this.list.itemClass : field.getType());
            }
        } else {
            this.type = annotation.type();
        }
        this.order = annotation.order();
        this.required = annotation.required();
        this.description = annotation.description();
    }

    private void processList(Class<?> declareType, Field field) {
        ListInfo info = new ListInfo();
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
            if (actualTypeArguments != null) {
                int length = actualTypeArguments.length;
                if (length != 1) {
                    throw new ProtoException("List must use generic definition like List<String>, please check field name '"
                            + field.getName() + "' at class " + field.getDeclaringClass().getName());
                }

                Type targetType = actualTypeArguments[0];
                if (targetType instanceof Class) {
                    info.itemClass = (Class) targetType;
                }
            }
        }

        if (info.itemClass == null) {
            ResolvableType resolvableType = ResolvableType.forClass(declareType);
            info.itemClass = resolvableType.getSuperType().getGeneric(0).resolve();
        }
        if (info.itemClass == null) {
            throw new ProtoException("Can't resolve generic type of field name '"
                    + field.getName() + "' at class " + field.getDeclaringClass().getName());
        }
        this.list = info;
    }

    private void processMap(Class<?> declareType, Field field) {
        MapInfo info = new MapInfo();
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType) type;
            Type[] actualTypeArguments = ptype.getActualTypeArguments();
            if (actualTypeArguments != null) {
                int length = actualTypeArguments.length;
                if (length != 2) {
                    throw new ProtoException(
                            "Map must use generic definiation like Map<String, String>, please check  field name '"
                                    + field.getName() + " at class " + field.getDeclaringClass().getName());
                }

                Type targetType = actualTypeArguments[0];
                if (targetType instanceof Class) {
                    info.keyClass = (Class) targetType;
                }
                targetType = actualTypeArguments[1];
                if (targetType instanceof Class) {
                    info.valueClass = (Class) targetType;
                }
            }
        }

        if (info.keyClass == null || info.valueClass == null) {
            // todo: 可能 Key/Value 类型中只有一个是泛型参数, 还有顺序问题
            ResolvableType resolvableType = ResolvableType.forClass(declareType);
            if (info.keyClass == null) {
                info.keyClass = resolvableType.getSuperType().getGeneric(0).resolve();
            }
            if (info.valueClass == null) {
                info.valueClass = resolvableType.getSuperType().getGeneric(1).resolve();
            }
        }
        info.keyType = FieldUtils.getFieldType(info.keyClass);
        info.valueType = FieldUtils.getFieldType(info.valueClass);
//        if (info.keyType == FieldType.AUTO) {
//            info.keyType = FieldUtils.getFieldType(info.keyClass);
//        }
//        if (info.valueType == FieldType.AUTO) {
//            info.valueType = FieldUtils.getFieldType(info.valueClass);
//        }
        this.map = info;
    }

    public Field getField() {
        return field;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class ListInfo {
        private Class<?> itemClass;

        public Class<?> getItemClass() {
            return itemClass;
        }
    }

    public static class MapInfo {
        private Class<?> keyClass;
        private Class<?> valueClass;
        private FieldType keyType;
        private FieldType valueType;

        public Class<?> getKeyClass() {
            return keyClass;
        }

        public Class<?> getValueClass() {
            return valueClass;
        }

        public FieldType getKeyType() {
            return keyType;
        }

        public FieldType getValueType() {
            return valueType;
        }
    }
}
