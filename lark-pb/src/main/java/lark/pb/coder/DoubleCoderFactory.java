package lark.pb.coder;

import lark.pb.field.FieldInfo;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * double 代码生成器工厂类
 * <p>
 *
 * @author cuigh
 */
public class DoubleCoderFactory implements CoderFactory {
    @Override
    public Coder get(Class<?> clazz) {
        if (clazz == double.class) {
            return DoubleCoder.INSTANCE;
        } else if (clazz == Double.class) {
            return ObjectDoubleCoder.INSTANCE;
        }
        return null;
    }

    private static class DoubleCoder extends SimpleCoder {
        private static final Coder INSTANCE = new DoubleCoder();

        private DoubleCoder() {
            super("Double", "Double");
            this.sizeMethod = "computeDoubleSize";
            this.sizeDescriptor = "(ID)I";
            this.writeMethod = "writeDouble";
            this.writeDescriptor = "(ID)V";
            this.readMethod = "readDouble";
            this.readDescriptor = "()D";
        }
    }

    private static class ObjectDoubleCoder extends DoubleCoder {
        private static final Coder INSTANCE = new ObjectDoubleCoder();

        @Override
        protected void convertTo(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKEVIRTUAL, Types.DOUBLE, "doubleValue", "()D", false);
        }

        @Override
        protected void convertFrom(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKESTATIC, Types.DOUBLE, "valueOf", "(D)Ljava/lang/Double;", false);
        }
    }
}
