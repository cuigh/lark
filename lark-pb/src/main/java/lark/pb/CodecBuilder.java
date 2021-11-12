package lark.pb;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import lark.core.util.Beans;
import lark.pb.coder.Coder;
import lark.pb.coder.CoderManager;
import lark.pb.coder.Types;
import lark.pb.field.FieldInfo;
import lark.pb.field.FieldUtils;
import org.objectweb.asm.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.*;

/**
 * 编解码器生成器
 *
 * @author cuigh
 */
public class CodecBuilder {
    public static byte[] build(Class<?> clazz) {
        BuildContext ctx = new BuildContext(clazz);
        ClassWriter cw = createClassWriter(ctx);
        List<FieldInfo> fields = FieldUtils.getProtoFields(clazz);

        // ctor
        buildConstructor(cw);

        // size
        buildSize(cw, ctx, fields);

        // encodeTo
        buildEncodeTo(cw, ctx, fields);

        // decodeFrom
        buildDecodeFrom(cw, ctx, fields);

        // getDescriptor
//        buildGetDescriptor(cw, ctx);

        buildSizeEntry(cw, ctx);

        buildEncodeToEntry(cw, ctx);

        buildDecodeFromEntry(cw, ctx);

        cw.visitEnd();

        return cw.toByteArray();
    }

    private static ClassWriter createClassWriter(BuildContext ctx) {
        ClassWriter cw = new ClassWriter(COMPUTE_FRAMES);
        String signature = String.format("Llark/pb/BaseCodec<%s>;", ctx.getBeanDescriptor());
        String superName = Type.getInternalName(BaseCodec.class);
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, ctx.getCodecClassName(), signature, superName, null);
        return cw;
    }

    private static void buildConstructor(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, Types.BASE_CODEC, "<init>", "()V", false);
        mv.visitInsn(RETURN);
//        mv.visitMaxs(1, 1);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private static void buildSize(ClassWriter cw, BuildContext ctx, List<FieldInfo> fields) {
        String descriptor = String.format("(%s)I", ctx.getBeanDescriptor());
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "size", descriptor, null, null);
        mv.visitParameter("t", 0);
        mv.visitCode();

        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 2);

        boolean first = true;
        for (FieldInfo f : fields) {
            Coder coder = CoderManager.get(f);
            if (f.getField().getType().isPrimitive()) {
                coder.size(mv, f, ctx);
            } else {
                // 判读是否为 null
                mv.visitVarInsn(ALOAD, 1);
                readField(mv, f, ctx.getBeanClassName());
                Label label = new Label();
                mv.visitJumpInsn(IFNULL, label);

                // 计算大小
                coder.size(mv, f, ctx);

                // 结束判断
                mv.visitLabel(label);
                if (first) {
                    mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.INTEGER}, 0, null);
                    first = false;
                } else {
                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                }
            }
        }

        mv.visitVarInsn(ILOAD, 2);
        mv.visitInsn(IRETURN);
//        mv.visitMaxs(4, 3);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private static void buildEncodeTo(ClassWriter cw, BuildContext ctx, List<FieldInfo> fields) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC,
                "encodeTo",
                String.format("(%s%s)V", ctx.getBeanDescriptor(), Type.getDescriptor(CodedOutputStream.class)),
                null,
                new String[]{Type.getInternalName(IOException.class)});
        mv.visitCode();

        buildCheckNullBlock(mv, ctx, fields, 1);

        for (FieldInfo f : fields) {
            Coder coder = CoderManager.get(f);
            if (f.isRequired() || f.getField().getType().isPrimitive()) {
                coder.write(mv, f, ctx);
            } else {
                // 判读是否为 null
                mv.visitVarInsn(ALOAD, 1);
                readField(mv, f, ctx.getBeanClassName());
                Label label = new Label();
                mv.visitJumpInsn(IFNULL, label);
                // 写入数据
                coder.write(mv, f, ctx);
                // 结束判断
                mv.visitLabel(label);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            }
        }

        mv.visitInsn(RETURN);
//        mv.visitMaxs(4, 3);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private static void buildDecodeFrom(ClassWriter cw, BuildContext ctx, List<FieldInfo> fields) {
        String desc = String.format("(%s)%s", Type.getDescriptor(CodedInputStream.class), ctx.getBeanDescriptor());
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "decodeFrom", desc, null, new String[]{Type.getInternalName(IOException.class)});
        mv.visitCode();

        mv.visitTypeInsn(NEW, ctx.getBeanClassName());
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, ctx.getBeanClassName(), "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, 2);

        // while
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{ctx.getBeanClassName()}, 0, null);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, Types.CODED_INPUT_STREAM, "readTag", "()I", false);
        mv.visitVarInsn(ISTORE, 3);
        mv.visitVarInsn(ILOAD, 3);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);
        Label l2 = new Label();
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l1);
        mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.INTEGER}, 0, null);

        // read
        for (FieldInfo f : fields) {
            Coder coder = CoderManager.get(f);
            coder.read(mv, f, ctx, l0);
        }

        // skip field
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ILOAD, 3);
        mv.visitMethodInsn(INVOKEVIRTUAL, Types.CODED_INPUT_STREAM, "skipField", "(I)Z", false);
        mv.visitInsn(POP);
        mv.visitJumpInsn(GOTO, l0);
        mv.visitLabel(l2);
        mv.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);

        // checkNull
        buildCheckNullBlock(mv, ctx, fields, 2);

        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ARETURN);
//        mv.visitMaxs(3, 4);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

//    private static void buildGetDescriptor(ClassWriter cw, BuildContext ctx) {
//        String fieldDesc = Type.getDescriptor(Descriptors.Descriptor.class);
//        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "getDescriptor", "()Lcom/google/protobuf/Descriptors$Descriptor;", null, new String[]{Types.IOException});
//        mv.visitCode();
//        mv.visitVarInsn(ALOAD, 0);
//        mv.visitFieldInsn(GETFIELD, ctx.getCodecClassName(), "descriptor", fieldDesc);
//        Label l0 = new Label();
//        mv.visitJumpInsn(IFNONNULL, l0);
//        mv.visitVarInsn(ALOAD, 0);
//        mv.visitLdcInsn(Type.getType(ctx.getBeanDescriptor()));
//        String methodDesc = getMethodDescriptor(CodedConstant.class, true, "getDescriptor", Class.class);
//        mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(CodedConstant.class), "getDescriptor", methodDesc, false);
//        mv.visitFieldInsn(PUTFIELD, ctx.getCodecClassName(), "descriptor", fieldDesc);
//        mv.visitLabel(l0);
//        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
//        mv.visitVarInsn(ALOAD, 0);
//        mv.visitFieldInsn(GETFIELD, ctx.getCodecClassName(), "descriptor", fieldDesc);
//        mv.visitInsn(ARETURN);
////        mv.visitMaxs(2, 1);
//        mv.visitMaxs(0, 0);
//        mv.visitEnd();
//    }

    private static void buildSizeEntry(ClassWriter cw, BuildContext ctx) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "size", "(Ljava/lang/Object;)I", null, new String[]{Types.IOEXCEPTION});
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, ctx.getBeanClassName());
        mv.visitMethodInsn(INVOKEVIRTUAL, ctx.getCodecClassName(), "size", String.format("(%s)I", ctx.getBeanDescriptor()), false);
        mv.visitInsn(IRETURN);
//        mv.visitMaxs(2, 2);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private static void buildEncodeToEntry(ClassWriter cw, BuildContext ctx) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "encodeTo", "(Ljava/lang/Object;Lcom/google/protobuf/CodedOutputStream;)V", null, new String[]{Types.IOEXCEPTION});
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, ctx.getBeanClassName());
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKEVIRTUAL, ctx.getCodecClassName(), "encodeTo", String.format("(%sLcom/google/protobuf/CodedOutputStream;)V", ctx.getBeanDescriptor()), false);
        mv.visitInsn(RETURN);
//        mv.visitMaxs(3, 3);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private static void buildDecodeFromEntry(ClassWriter cw, BuildContext ctx) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "decodeFrom", "(Lcom/google/protobuf/CodedInputStream;)Ljava/lang/Object;", null, new String[]{Types.IOEXCEPTION});
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, ctx.getCodecClassName(), "decodeFrom", String.format("(Lcom/google/protobuf/CodedInputStream;)%s", ctx.getBeanDescriptor()), false);
        mv.visitInsn(ARETURN);
//        mv.visitMaxs(2, 2);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private static void buildCheckNullBlock(MethodVisitor mv, BuildContext ctx, List<FieldInfo> fields, int varIndex) {
        for (FieldInfo f : fields) {
            if (!f.isRequired() || f.getField().getType().isPrimitive()) {
                continue;
            }

            mv.visitLdcInsn(f.getField().getName());
            mv.visitVarInsn(ALOAD, varIndex);
            readField(mv, f, ctx.getBeanClassName());
            String desc = getMethodDescriptor(BaseCodec.class, false, "checkNull", String.class, Object.class);
            mv.visitMethodInsn(INVOKESTATIC, ctx.getCodecClassName(), "checkNull", desc, false);
        }
    }

    private static String getMethodDescriptor(Class<?> cls, boolean isPublic, String name, Class<?>... paramTypes) {
        try {
            Method method = isPublic ? cls.getMethod(name, paramTypes) : cls.getDeclaredMethod(name, paramTypes);
            return Type.getMethodDescriptor(method);
        } catch (NoSuchMethodException e) {
            throw new ProtoException(e);
        }
    }

    private static void readField(MethodVisitor mv, FieldInfo f, String className) {
        if (f.getField().getModifiers() == Modifier.PUBLIC) {
            mv.visitFieldInsn(GETFIELD, className, f.getField().getName(), Type.getDescriptor(f.getField().getType()));
        } else {
            String desc = String.format("()%s", Type.getDescriptor(f.getField().getType()));
            mv.visitMethodInsn(INVOKEVIRTUAL, className, Beans.getGetterName(f.getField()), desc, false);
        }
    }
}
