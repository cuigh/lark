package lark.pb.coder;

import lark.pb.field.FieldInfo;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * int32 代码生成器工厂类
 *
 * @author cuigh
 */
public class Int32CoderFactory implements CoderFactory {
    @Override
    public Coder get(Class<?> clazz) {
        if (clazz == int.class) {
            return Int32Coder.INSTANCE;
        } else if (clazz == Integer.class) {
            return ObjectInt32Coder.INSTANCE;
        }
        return null;
    }

    private static class Int32Coder extends SimpleCoder {
        private static final Coder INSTANCE = new Int32Coder();

        private Int32Coder() {
            super("Int32", "Int32");
            this.sizeMethod = "computeInt32Size";
            this.sizeDescriptor = "(II)I";
            this.writeMethod = "writeInt32";
            this.writeDescriptor = "(II)V";
            this.readMethod = "readInt32";
            this.readDescriptor = "()I";
        }
    }

    private static class ObjectInt32Coder extends Int32Coder {
        private static final Coder INSTANCE = new ObjectInt32Coder();

        @Override
        protected void convertTo(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKEVIRTUAL, Types.INTEGER, "intValue", "()I", false);
        }

        @Override
        protected void convertFrom(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKESTATIC, Types.INTEGER, "valueOf", "(I)Ljava/lang/Integer;", false);
        }
    }
}
