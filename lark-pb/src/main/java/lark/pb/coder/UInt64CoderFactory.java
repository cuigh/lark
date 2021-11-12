package lark.pb.coder;

import lark.pb.field.FieldInfo;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * uint64 代码生成器工厂类
 *
 * @author cuigh
 */
public class UInt64CoderFactory implements CoderFactory {
    @Override
    public Coder get(Class<?> clazz) {
        if (clazz == long.class) {
            return UInt64Coder.INSTANCE;
        } else if (clazz == Long.class) {
            return ObjectUInt64Coder.INSTANCE;
        }
        return null;
    }

    private static class UInt64Coder extends SimpleCoder {
        private static final Coder INSTANCE = new UInt64Coder();

        private UInt64Coder() {
            super("UInt64", "UInt64");
            this.sizeMethod = "computeUInt64Size";
            this.sizeDescriptor = "(IJ)I";
            this.writeMethod = "writeUInt64";
            this.writeDescriptor = "(IJ)V";
            this.readMethod = "readUInt64";
            this.readDescriptor = "()J";
        }
    }

    private static class ObjectUInt64Coder extends UInt64Coder {
        private static final Coder INSTANCE = new ObjectUInt64Coder();

        @Override
        protected void convertTo(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKEVIRTUAL, Types.LONG, "longValue", "()J", false);
        }

        @Override
        protected void convertFrom(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKESTATIC, Types.LONG, "valueOf", "(J)Ljava/lang/Long;", false);
        }
    }

}
