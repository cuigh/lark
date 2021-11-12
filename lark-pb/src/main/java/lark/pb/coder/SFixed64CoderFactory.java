package lark.pb.coder;

import lark.pb.field.FieldInfo;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * sfixed64 代码生成器工厂类
 *
 * @author cuigh
 */
public class SFixed64CoderFactory implements CoderFactory {
    @Override
    public Coder get(Class<?> clazz) {
        if (clazz == long.class) {
            return SFixed64CoderFactory.SFixed64Coder.INSTANCE;
        } else if (clazz == Long.class) {
            return SFixed64CoderFactory.ObjectSFixed64Coder.INSTANCE;
        }
        return null;
    }

    private static class SFixed64Coder extends SimpleCoder {
        private static final Coder INSTANCE = new SFixed64CoderFactory.SFixed64Coder();

        private SFixed64Coder() {
            super("SFixed64", "SFixed64");
            this.sizeMethod = "computeSFixed64Size";
            this.sizeDescriptor = "(IJ)I";
            this.writeMethod = "writeSFixed64";
            this.writeDescriptor = "(IJ)V";
            this.readMethod = "readSFixed64";
            this.readDescriptor = "()J";
        }
    }

    private static class ObjectSFixed64Coder extends SFixed64CoderFactory.SFixed64Coder {
        private static final Coder INSTANCE = new SFixed64CoderFactory.ObjectSFixed64Coder();

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
