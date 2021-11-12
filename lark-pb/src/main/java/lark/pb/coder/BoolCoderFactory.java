package lark.pb.coder;

import lark.pb.field.FieldInfo;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * bool 代码生成器工厂类
 *
 * @author cuigh
 */
public class BoolCoderFactory implements CoderFactory {
    @Override
    public Coder get(Class<?> clazz) {
        if (clazz == boolean.class) {
            return BoolCoderFactory.BoolCoder.INSTANCE;
        } else if (clazz == Boolean.class) {
            return BoolCoderFactory.ObjectBoolCoder.INSTANCE;
        }
        return null;
    }

    private static class BoolCoder extends SimpleCoder {
        private static final Coder INSTANCE = new BoolCoder();

        private BoolCoder() {
            super("Bool", "Bool");
            this.sizeMethod = "computeBoolSize";
            this.sizeDescriptor = "(IZ)I";
            this.writeMethod = "writeBool";
            this.writeDescriptor = "(IZ)V";
            this.readMethod = "readBool";
            this.readDescriptor = "()Z";
        }
    }

    private static class ObjectBoolCoder extends BoolCoder {
        private static final Coder INSTANCE = new ObjectBoolCoder();

        @Override
        protected void convertTo(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKEVIRTUAL, Types.BOOLEAN, "booleanValue", "()Z", false);
        }

        @Override
        protected void convertFrom(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKESTATIC, Types.BOOLEAN, "valueOf", "(Z)Ljava/lang/Boolean;", false);
        }
    }
}
