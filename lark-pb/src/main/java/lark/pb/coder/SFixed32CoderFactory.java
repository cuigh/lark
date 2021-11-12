package lark.pb.coder;

import lark.pb.field.FieldInfo;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * fixed32 代码生成器工厂类
 *
 * @author cuigh
 */
public class SFixed32CoderFactory implements CoderFactory {
    @Override
    public Coder get(Class<?> clazz) {
        if (clazz == int.class) {
            return SFixed32Coder.INSTANCE;
        } else if (clazz == Integer.class) {
            return ObjectSFixed32Coder.INSTANCE;
        }
        return null;
    }

    private static class SFixed32Coder extends SimpleCoder {
        private static final Coder INSTANCE = new SFixed32Coder();

        private SFixed32Coder() {
            super("SFixed32", "SFixed32");
            this.sizeMethod = "computeSFixed32Size";
            this.sizeDescriptor = "(II)I";
            this.writeMethod = "writeSFixed32";
            this.writeDescriptor = "(II)V";
            this.readMethod = "readSFixed32";
            this.readDescriptor = "()I";
        }
    }

    private static class ObjectSFixed32Coder extends SFixed32Coder {
        private static final Coder INSTANCE = new ObjectSFixed32Coder();

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
