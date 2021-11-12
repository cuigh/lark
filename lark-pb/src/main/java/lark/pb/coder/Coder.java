package lark.pb.coder;

import lark.core.util.Beans;
import lark.pb.BuildContext;
import lark.pb.CodecUtils;
import lark.pb.field.FieldInfo;
import lark.pb.field.FieldKind;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Modifier;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author cuigh
 */
public abstract class Coder {
    /**
     * Int32, Date...
     */
    String realType;
    /**
     * Int32, Int64...
     */
    String protoType;
    private String helperType;

    protected Coder(String realType, String protoType) {
        this.realType = realType;
        this.protoType = protoType;
        this.helperType = String.format("lark/pb/helper/%sHelper", realType);
    }

    static void buildVisitInt(MethodVisitor mv, int value) {
        if (value >= 0 && value <= 5) {
            mv.visitInsn(ICONST_0 + value);
        } else if (value >= -128 && value <= 127) {
            mv.visitIntInsn(BIPUSH, value);
        } else if (value >= -32768 && value <= 32767) {
            mv.visitIntInsn(SIPUSH, value);
        } else {
            // (value >= -2147483648 && value <= 2147483647)
            mv.visitLdcInsn(value);
        }
    }

    static void readField(MethodVisitor mv, FieldInfo fi, String className) {
        if (fi.getField().getModifiers() == Modifier.PUBLIC) {
            mv.visitFieldInsn(GETFIELD, className, fi.getField().getName(), Type.getDescriptor(fi.getField().getType()));
        } else {
            String desc = String.format("()%s", Type.getDescriptor(fi.getField().getType()));
            mv.visitMethodInsn(INVOKEVIRTUAL, className, Beans.getGetterName(fi.getField()), desc, false);
        }
    }

    static void writeField(MethodVisitor mv, FieldInfo fi, String className) {
        if (fi.getField().getModifiers() == Modifier.PUBLIC) {
            mv.visitFieldInsn(PUTFIELD, className, fi.getField().getName(), Type.getDescriptor(fi.getField().getType()));
        } else {
            String desc = String.format("(%s)V", Type.getDescriptor(fi.getField().getType()));
            mv.visitMethodInsn(INVOKEVIRTUAL, className, Beans.getSetterName(fi.getField()), desc, false);
        }
    }

    public void size(MethodVisitor mv, FieldInfo fi, BuildContext ctx) {
        mv.visitVarInsn(ILOAD, 2);
        buildVisitInt(mv, fi.getOrder());

        mv.visitVarInsn(ALOAD, 1);
        readField(mv, fi, ctx.getBeanClassName());

        if (fi.getKind() == FieldKind.LIST) {
            buildListSizeCode(mv, fi, ctx);
        } else {
            buildSizeCode(mv, fi, ctx);
        }

        mv.visitInsn(IADD);
        mv.visitVarInsn(ISTORE, 2);
    }

    public void write(MethodVisitor mv, FieldInfo fi, BuildContext ctx) {
        mv.visitVarInsn(ALOAD, 2);
        buildVisitInt(mv, fi.getOrder());

        mv.visitVarInsn(ALOAD, 1);
        readField(mv, fi, ctx.getBeanClassName());

        if (fi.getKind() == FieldKind.LIST) {
            buildListWriteCode(mv, fi, ctx);
        } else {
            buildWriteCode(mv, fi, ctx);
        }
    }

    public void read(MethodVisitor mv, FieldInfo fi, BuildContext ctx, Label label) {
        mv.visitVarInsn(ILOAD, 3);
        int tag = CodecUtils.makeTag(fi.getOrder(), fi.getType().getProtoFieldType().getWireType());
        buildVisitInt(mv, tag);

        Label lbl = new Label();
        mv.visitJumpInsn(IF_ICMPNE, lbl);
        mv.visitVarInsn(ALOAD, 2);

        if (fi.getKind() == FieldKind.LIST) {
            // 判断属性是否已经初始化
            readField(mv, fi, ctx.getBeanClassName());
            Label labelList = new Label();
            mv.visitJumpInsn(IFNONNULL, labelList);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(NEW, Types.ARRAY_LIST);
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, Types.ARRAY_LIST, "<init>", "()V", false);
            writeField(mv, fi, ctx.getBeanClassName());
            mv.visitLabel(labelList);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

            buildListReadCode(mv, fi, ctx);
        } else {
            buildReadCode(mv, fi, ctx);
        }

        mv.visitJumpInsn(GOTO, label);
        mv.visitLabel(lbl);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    }

    protected abstract void buildSizeCode(MethodVisitor mv, FieldInfo fi, BuildContext ctx);

    protected void buildListSizeCode(MethodVisitor mv, FieldInfo fi, BuildContext ctx) {
    }

    protected abstract void buildWriteCode(MethodVisitor mv, FieldInfo fi, BuildContext ctx);

    protected void buildListWriteCode(MethodVisitor mv, FieldInfo fi, BuildContext ctx) {
    }

    protected abstract void buildReadCode(MethodVisitor mv, FieldInfo fi, BuildContext ctx);

    protected void buildListReadCode(MethodVisitor mv, FieldInfo fi, BuildContext ctx) {
    }

    String getHelperType() {
        return this.helperType;
    }
}
