package lark.pb.coder;

import lark.pb.field.FieldInfo;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * float 代码生成器工厂类
 *
 * @author cuigh
 */
public class FloatCoderFactory implements CoderFactory {
    @Override
    public Coder get(Class<?> clazz) {
        if (clazz == float.class) {
            return FloatCoder.INSTANCE;
        } else if (clazz == Float.class) {
            return ObjectFloatCoder.INSTANCE;
        }
        return null;
    }

    private static class FloatCoder extends SimpleCoder {
        private static final Coder INSTANCE = new FloatCoder();

        private FloatCoder() {
            super("Float", "Float");
            this.sizeMethod = "computeFloatSize";
            this.sizeDescriptor = "(IF)I";
            this.writeMethod = "writeFloat";
            this.writeDescriptor = "(IF)V";
            this.readMethod = "readFloat";
            this.readDescriptor = "()F";
        }
    }

    private static class ObjectFloatCoder extends FloatCoder {
        private static final Coder INSTANCE = new ObjectFloatCoder();

        @Override
        protected void convertTo(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKEVIRTUAL, Types.FLOAT, "floatValue", "()F", false);
        }

        @Override
        protected void convertFrom(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKESTATIC, Types.FLOAT, "valueOf", "(F)Ljava/lang/Float;", false);
        }
    }
}
