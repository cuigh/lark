package lark.pb.coder;

import lark.pb.field.FieldInfo;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * uint32 代码生成器工厂类
 *
 * @author cuigh
 */
public class UInt32CoderFactory implements CoderFactory {
    @Override
    public Coder get(Class<?> clazz) {
        if (clazz == int.class) {
            return UInt32Coder.INSTANCE;
        } else if (clazz == Integer.class) {
            return ObjectUInt32Coder.INSTANCE;
        }
        return null;
    }

    private static class UInt32Coder extends SimpleCoder {
        private static final Coder INSTANCE = new UInt32Coder();

        private UInt32Coder() {
            super("UInt32", "UInt32");
            this.sizeMethod = "computeUInt32Size";
            this.sizeDescriptor = "(II)I";
            this.writeMethod = "writeUInt32";
            this.writeDescriptor = "(II)V";
            this.readMethod = "readUInt32";
            this.readDescriptor = "()I";
        }
    }

    private static class ObjectUInt32Coder extends UInt32Coder {
        private static final Coder INSTANCE = new ObjectUInt32Coder();

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
