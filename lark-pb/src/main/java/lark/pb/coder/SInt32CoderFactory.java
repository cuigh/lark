package lark.pb.coder;

import lark.pb.field.FieldInfo;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * sint32 代码生成器工厂类
 *
 * @author cuigh
 */
public class SInt32CoderFactory implements CoderFactory {
    @Override
    public Coder get(Class<?> clazz) {
        if (clazz == int.class) {
            return SInt32Coder.INSTANCE;
        } else if (clazz == Integer.class) {
            return ObjectSInt32Coder.INSTANCE;
        }
        return null;
    }

    private static class SInt32Coder extends SimpleCoder {
        private static final Coder INSTANCE = new SInt32Coder();

        private SInt32Coder() {
            super("SInt32", "SInt32");
            this.sizeMethod = "computeSInt32Size";
            this.sizeDescriptor = "(II)I";
            this.writeMethod = "writeSInt32";
            this.writeDescriptor = "(II)V";
            this.readMethod = "readSInt32";
            this.readDescriptor = "()I";
        }
    }

    private static class ObjectSInt32Coder extends SInt32Coder {
        private static final Coder INSTANCE = new ObjectSInt32Coder();

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
