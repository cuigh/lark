package lark.pb.coder;

import lark.pb.field.FieldInfo;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * fixed64 代码生成器工厂类
 *
 * @author cuigh
 */
public class Fixed64CoderFactory implements CoderFactory {
    @Override
    public Coder get(Class<?> clazz) {
        if (clazz == long.class) {
            return Fixed64CoderFactory.Fixed64Coder.INSTANCE;
        } else if (clazz == Long.class) {
            return Fixed64CoderFactory.ObjectFixed64Coder.INSTANCE;
        }
        return null;
    }

    private static class Fixed64Coder extends SimpleCoder {
        private static final Coder INSTANCE = new Fixed64CoderFactory.Fixed64Coder();

        private Fixed64Coder() {
            super("Fixed64", "Fixed64");
            this.sizeMethod = "computeFixed64Size";
            this.sizeDescriptor = "(IJ)I";
            this.writeMethod = "writeFixed64";
            this.writeDescriptor = "(IJ)V";
            this.readMethod = "readFixed64";
            this.readDescriptor = "()J";
        }
    }

    private static class ObjectFixed64Coder extends Fixed64CoderFactory.Fixed64Coder {
        private static final Coder INSTANCE = new Fixed64CoderFactory.ObjectFixed64Coder();

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
