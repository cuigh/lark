package lark.net.rpc.service;

import lark.core.lang.EnumValuable;
import lark.net.rpc.annotation.RpcMethod;
import lark.net.rpc.annotation.RpcService;
import lark.net.rpc.protocol.simple.SimpleEncoder;
import lark.net.rpc.server.ServiceInfo;
import lark.pb.annotation.ProtoField;
import lark.pb.annotation.ProtoMessage;
import lark.pb.field.FieldInfo;
import lark.pb.field.FieldKind;
import lark.pb.field.FieldUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cuigh
 */
@RpcService(name = "$meta", description = "元数据服务")
public interface MetaService {
    @RpcMethod(description = "获取服务列表")
    ServiceList getServiceList();

    @RpcMethod(description = "获取服务信息")
    Service getService(String serviceName);

    @RpcMethod(description = "获取方法信息")
    Method getMethod(String serviceName, String methodName);

    @RpcMethod(description = "获取类型信息")
    Type getType(String typeID);

    @ProtoMessage(description = "获取服务列表")
    class ServiceList {
        @ProtoField(order = 1, required = false, description = "服务列表")
        public List<Service> services;
    }

    @ProtoMessage(description = "服务信息")
    class Service {
        @ProtoField(order = 1, required = true, description = "名称")
        public String name;

        @ProtoField(order = 2, required = false, description = "描述")
        public String description;

        @ProtoField(order = 3, required = false, description = "方法列表")
        public List<Method> methods;

        public Service() {
            // for decode
        }

        public Service(ServiceInfo s, boolean includeMethods) {
            this.name = s.getName();
            this.description = s.getDescription();
            if (includeMethods && !CollectionUtils.isEmpty(s.getMethods())) {
                this.methods = new ArrayList<>(s.getMethods().size());
                s.getMethods().forEach((n, m) -> this.methods.add(new Method(m)));
            }
        }
    }

    @ProtoMessage(description = "方法信息")
    class Method {
        @ProtoField(order = 1, required = true, description = "名称")
        public String name;

        @ProtoField(order = 2, required = false, description = "描述")
        public String description;

        @ProtoField(order = 3, required = false, description = "参数列表")
        public List<Parameter> parameters;

        @ProtoField(order = 4, required = false, description = "返回值")
        public Parameter returnType;

        public Method() {
            // for decode
        }

        public Method(ServiceInfo.MethodInfo m) {
            this.name = m.getName();
            this.description = m.getDescription();
            if (!CollectionUtils.isEmpty(m.getParameters())) {
                this.parameters = new ArrayList<>(m.getParameters().size());
                m.getParameters().forEach(p -> this.parameters.add(new Parameter(p)));
            }
            if (m.getReturnType() != null) {
                this.returnType = new Parameter(m.getReturnType());
            }
        }
    }

    @ProtoMessage(description = "参数信息")
    class Parameter {
        @ProtoField(order = 1, required = false, description = "名称")
        public String name;

        @ProtoField(order = 2, required = true, description = "类型")
        public Type type;

        @ProtoField(order = 3, required = false, description = "描述")
        public String description;

        public Parameter() {
            // for decode
        }

        public Parameter(ServiceInfo.ParameterInfo p) {
            this.name = p.getName();
            this.type = new Type(p.getType(), false);
            this.description = p.getDescription();
        }
    }

    @ProtoMessage(description = "类型信息")
    class Type {
        @ProtoField(order = 1, required = true, description = "标识")
        public String id;

        @ProtoField(order = 2, required = true, description = "名称")
        public String name;

        @ProtoField(order = 3, required = true, description = "类别, 对应通讯协议数据类型枚举")
        public int kind;

        @ProtoField(order = 4, required = false, description = "描述")
        public String description;

        @ProtoField(order = 5, required = false, description = "字段列表")
        public List<Field> fields;

        @ProtoField(order = 6, required = false, description = "枚举值列表")
        public List<Enum> enums;

        public Type() {
            // for decode
        }

        public Type(Class<?> clazz, boolean includeFields) {
            this.id = clazz.getName();
            this.name = clazz.getSimpleName();
            this.kind = SimpleEncoder.getDataType(clazz);
            if (this.kind == SimpleEncoder.DT_PROTOBUF) {
                ProtoMessage annotation = clazz.getAnnotation(ProtoMessage.class);
                if (annotation != null) {
                    this.description = annotation.description();
                }
                if (includeFields) {
                    this.fillFields(clazz);
                }
            }
        }

        private void fillFields(Class<?> clazz) {
            if (clazz.isEnum()) {
                Object[] constants = clazz.getEnumConstants();
                this.enums = new ArrayList<>(constants.length);
                for (Object c : constants) {
                    Enum e = new Enum(c);
                    this.enums.add(e);
                }
            } else {
                List<FieldInfo> infos = FieldUtils.getProtoFields(clazz);
                this.fields = new ArrayList<>(infos.size());
                infos.forEach(f -> this.fields.add(new Field(f)));
            }
        }
    }

    @ProtoMessage(description = "字段信息")
    class Field {
        @ProtoField(order = 1, required = true, description = "名称")
        public String name;

        @ProtoField(order = 2, required = true, description = "数据类型")
        public Type type;

        @ProtoField(order = 3, required = false, description = "描述")
        public String description;

        @ProtoField(order = 4, required = true, description = "顺序")
        public int order;

        @ProtoField(order = 5, required = true, description = "修饰符, 1-optional, 2-required, 3-repeated")
        public int modifier;

        @ProtoField(order = 6, required = true, description = "类别, 对应序列化数据类型枚举")
        public int kind;

        public Field() {
            // for decode
        }

        public Field(FieldInfo f) {
            this.name = f.getField().getName();
            Class<?> clazz = f.getKind() == FieldKind.LIST ? f.getList().getItemClass() : f.getField().getType();
            this.type = new Type(clazz, clazz != Type.class);   // 对 Type 类型做特殊处理, 防止死循环
            this.description = f.getDescription();
            this.order = f.getOrder();
            this.kind = f.getType().getKind();
            if (f.getKind() == FieldKind.LIST) {
                this.modifier = 3;
            } else if (f.isRequired()) {
                this.modifier = 2;
            } else {
                this.modifier = 1;
            }
        }
    }

    @ProtoMessage(description = "枚举信息")
    class Enum {
        @ProtoField(order = 1, required = true, description = "名称")
        public String name;

        @ProtoField(order = 2, required = true, description = "值")
        public int value;

        public Enum() {
            // for decode
        }

        public Enum(Object value) {
            java.lang.Enum e = (java.lang.Enum) value;
            this.name = e.name();
            this.value = ((EnumValuable) e).value();
        }
    }
}
