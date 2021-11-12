package lark.pb.coder;

import lark.pb.BuildContext;
import lark.pb.field.FieldInfo;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

/**
 * 简单代码生成器
 *
 * @author cuigh
 */
public abstract class SimpleCoder extends Coder {
    String sizeMethod;
    String sizeDescriptor;
    String writeMethod;
    String writeDescriptor;
    String readMethod;
    String readDescriptor;

    protected SimpleCoder(String realType, String protoType) {
        super(realType, protoType);
    }

    @Override
    protected void buildSizeCode(MethodVisitor mv, FieldInfo fi, BuildContext ctx) {
        convertTo(mv, fi);
        mv.visitMethodInsn(INVOKESTATIC, Types.CODED_OUTPUT_STREAM, this.sizeMethod, this.sizeDescriptor, false);
    }

    @Override
    protected void buildListSizeCode(MethodVisitor mv, FieldInfo fi, BuildContext ctx) {
        mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "sizeList", "(ILjava/util/List;)I", false);
    }

    @Override
    protected void buildWriteCode(MethodVisitor mv, FieldInfo fi, BuildContext ctx) {
        convertTo(mv, fi);
        mv.visitMethodInsn(INVOKEVIRTUAL, Types.CODED_OUTPUT_STREAM, this.writeMethod, this.writeDescriptor, false);
    }

    @Override
    protected void buildListWriteCode(MethodVisitor mv, FieldInfo fi, BuildContext ctx) {
        mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "writeList", "(Lcom/google/protobuf/CodedOutputStream;ILjava/util/List;)V", false);
    }

    @Override
    protected void buildReadCode(MethodVisitor mv, FieldInfo fi, BuildContext ctx) {
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, Types.CODED_INPUT_STREAM, this.readMethod, this.readDescriptor, false);
        convertFrom(mv, fi);
        writeField(mv, fi, ctx.getBeanClassName());
    }

    @Override
    protected void buildListReadCode(MethodVisitor mv, FieldInfo fi, BuildContext ctx) {
        mv.visitVarInsn(ALOAD, 2);
        readField(mv, fi, ctx.getBeanClassName());
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, Types.CODED_INPUT_STREAM, this.readMethod, this.readDescriptor, false);
        convertFrom(mv, fi);
        mv.visitMethodInsn(INVOKEINTERFACE, Types.LIST, "add", "(Ljava/lang/Object;)Z", true);
        mv.visitInsn(POP);
    }

    /**
     * 生成从指定类型转换到简单类型的指令
     *
     * @param mv 方法访问者
     * @param fi 字段信息
     */
    protected void convertTo(MethodVisitor mv, FieldInfo fi) {
        // 给派生类提供注入口
    }

    /**
     * 生成从简单类型转换到指定类型的指令
     *
     * @param mv 方法访问者
     * @param fi 字段信息
     */
    protected void convertFrom(MethodVisitor mv, FieldInfo fi) {
        // 给派生类提供注入口
    }
}
