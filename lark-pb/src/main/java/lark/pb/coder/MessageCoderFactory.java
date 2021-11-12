package lark.pb.coder;

import lark.pb.BuildContext;
import lark.pb.field.FieldInfo;
import lark.pb.field.FieldType;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author cuigh
 */
public class MessageCoderFactory implements CoderFactory {
    @Override
    public Coder get(Class<?> clazz) {
        if (Map.class.isAssignableFrom(clazz)) {
            return MapCoder.INSTANCE;
        } else if (clazz.isPrimitive() || clazz.isEnum() || clazz.isAnnotation() || clazz.isInterface()) {
            return null;
        }
        return ObjectCoder.INSTANCE;
    }

    // todo: 从 SimpleCoder 继承
    private static class ObjectCoder extends Coder {
        private static final Coder INSTANCE = new ObjectCoder();

        private ObjectCoder() {
            super("Message", "Message");
        }

        @Override
        protected void buildSizeCode(MethodVisitor mv, FieldInfo fi, BuildContext ctx) {
            String descriptor = Type.getDescriptor(fi.getField().getType());

            mv.visitLdcInsn(Type.getType(descriptor));
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "size", "(ILjava/lang/Object;Ljava/lang/Class;)I", false);
        }

        @Override
        protected void buildListSizeCode(MethodVisitor mv, FieldInfo fi, BuildContext ctx) {
            String descriptor = Type.getDescriptor(fi.getList().getItemClass());

            mv.visitLdcInsn(Type.getType(descriptor));
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "sizeList", "(ILjava/util/List;Ljava/lang/Class;)I", false);
        }

        @Override
        protected void buildWriteCode(MethodVisitor mv, FieldInfo fi, BuildContext ctx) {
            String descriptor = Type.getDescriptor(fi.getField().getType());
            mv.visitLdcInsn(Type.getType(descriptor));
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "write", "(Lcom/google/protobuf/CodedOutputStream;ILjava/lang/Object;Ljava/lang/Class;)V", false);
        }

        @Override
        protected void buildListWriteCode(MethodVisitor mv, FieldInfo fi, BuildContext ctx) {
            String descriptor = Type.getDescriptor(fi.getList().getItemClass());
            mv.visitLdcInsn(Type.getType(descriptor));
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "writeList", "(Lcom/google/protobuf/CodedOutputStream;ILjava/util/List;Ljava/lang/Class;)V", false);
        }

        @Override
        protected void buildReadCode(MethodVisitor mv, FieldInfo fi, BuildContext ctx) {
            String type = Type.getInternalName(fi.getField().getType());
            String descriptor = "L" + type + ";";

            mv.visitVarInsn(ALOAD, 1);
            mv.visitLdcInsn(Type.getType(descriptor));
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "read", "(Lcom/google/protobuf/CodedInputStream;Ljava/lang/Class;)Ljava/lang/Object;", false);
            mv.visitTypeInsn(CHECKCAST, type);
            writeField(mv, fi, ctx.getBeanClassName());
        }

        @Override
        protected void buildListReadCode(MethodVisitor mv, FieldInfo fi, BuildContext ctx) {
            String descriptor = Type.getDescriptor(fi.getList().getItemClass());

            mv.visitVarInsn(ALOAD, 2);
            readField(mv, fi, ctx.getBeanClassName());
            mv.visitVarInsn(ALOAD, 1);
            mv.visitLdcInsn(Type.getType(descriptor));
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "read", "(Lcom/google/protobuf/CodedInputStream;Ljava/lang/Class;)Ljava/lang/Object;", false);
            mv.visitMethodInsn(INVOKEINTERFACE, Types.LIST, "add", "(Ljava/lang/Object;)Z", true);
            mv.visitInsn(POP);
        }
    }

    private static class MapCoder extends Coder {
        private static final Coder INSTANCE = new MapCoder();

        private MapCoder() {
            super("Map", "Message");
        }

        @Override
        protected void buildSizeCode(MethodVisitor mv, FieldInfo fi, BuildContext ctx) {
            FieldType keyType = fi.getMap().getKeyType();
            FieldType valueType = fi.getMap().getValueType();
            mv.visitFieldInsn(GETSTATIC, Types.FIELD_TYPE, keyType.getProtoFieldType().name(), Descriptors.WIRE_FORMAT_FIELD_TYPE);
            mv.visitFieldInsn(GETSTATIC, Types.FIELD_TYPE, valueType.getProtoFieldType().name(), Descriptors.WIRE_FORMAT_FIELD_TYPE);
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "size", "(ILjava/util/Map;Lcom/google/protobuf/WireFormat$FieldType;Lcom/google/protobuf/WireFormat$FieldType;)I", false);
        }

        @Override
        protected void buildWriteCode(MethodVisitor mv, FieldInfo fi, BuildContext ctx) {
            FieldType keyType = fi.getMap().getKeyType();
            FieldType valueType = fi.getMap().getValueType();
            mv.visitFieldInsn(GETSTATIC, Types.FIELD_TYPE, keyType.getProtoFieldType().name(), Descriptors.WIRE_FORMAT_FIELD_TYPE);
            mv.visitFieldInsn(GETSTATIC, Types.FIELD_TYPE, valueType.getProtoFieldType().name(), Descriptors.WIRE_FORMAT_FIELD_TYPE);
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "write", "(Lcom/google/protobuf/CodedOutputStream;ILjava/util/Map;Lcom/google/protobuf/WireFormat$FieldType;Lcom/google/protobuf/WireFormat$FieldType;)V", false);
        }

        @Override
        protected void buildReadCode(MethodVisitor mv, FieldInfo fi, BuildContext ctx) {
            readField(mv, fi, ctx.getBeanClassName());
            Label l17 = new Label();
            mv.visitJumpInsn(IFNONNULL, l17);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(NEW, Types.HASH_MAP);
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, Types.HASH_MAP, "<init>", "()V", false);
            writeField(mv, fi, ctx.getBeanClassName());
            mv.visitLabel(l17);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            readField(mv, fi, ctx.getBeanClassName());
            FieldType keyType = fi.getMap().getKeyType();
            FieldType valueType = fi.getMap().getValueType();
            mv.visitFieldInsn(GETSTATIC, Types.FIELD_TYPE, keyType.getProtoFieldType().name(), Descriptors.WIRE_FORMAT_FIELD_TYPE);
            mv.visitFieldInsn(GETSTATIC, Types.FIELD_TYPE, valueType.getProtoFieldType().name(), Descriptors.WIRE_FORMAT_FIELD_TYPE);
            mv.visitLdcInsn(Type.getType(Type.getDescriptor(fi.getMap().getKeyClass())));
            mv.visitLdcInsn(Type.getType(Type.getDescriptor(fi.getMap().getValueClass())));
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "read", "(Lcom/google/protobuf/CodedInputStream;Ljava/util/Map;Lcom/google/protobuf/WireFormat$FieldType;Lcom/google/protobuf/WireFormat$FieldType;Ljava/lang/Class;Ljava/lang/Class;)V", false);
        }
    }
}
