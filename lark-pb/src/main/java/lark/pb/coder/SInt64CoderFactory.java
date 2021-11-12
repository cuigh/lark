package lark.pb.coder;

import lark.pb.field.FieldInfo;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * sint64 代码生成器工厂类
 *
 * @author cuigh
 */
public class SInt64CoderFactory implements CoderFactory {
    @Override
    public Coder get(Class<?> clazz) {
        if (clazz == long.class) {
            return SInt64CoderFactory.SInt64Coder.INSTANCE;
        } else if (clazz == Long.class) {
            return SInt64CoderFactory.ObjectSInt64Coder.INSTANCE;
        }
        return null;
    }

    private static class SInt64Coder extends SimpleCoder {
        private static final Coder INSTANCE = new SInt64CoderFactory.SInt64Coder();

        private SInt64Coder() {
            super("SInt64", "SInt64");
            this.sizeMethod = "computeSInt64Size";
            this.sizeDescriptor = "(IJ)I";
            this.writeMethod = "writeSInt64";
            this.writeDescriptor = "(IJ)V";
            this.readMethod = "readSInt64";
            this.readDescriptor = "()J";
        }
    }

    private static class ObjectSInt64Coder extends SInt64CoderFactory.SInt64Coder {
        private static final Coder INSTANCE = new SInt64CoderFactory.ObjectSInt64Coder();

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
