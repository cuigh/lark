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
public class Fixed32CoderFactory implements CoderFactory {
    @Override
    public Coder get(Class<?> clazz) {
        if (clazz == int.class) {
            return Fixed32Coder.INSTANCE;
        } else if (clazz == Integer.class) {
            return ObjectFixed32Coder.INSTANCE;
        }
        return null;
    }

    private static class Fixed32Coder extends SimpleCoder {
        private static final Coder INSTANCE = new Fixed32Coder();

        private Fixed32Coder() {
            super("Fixed32", "Fixed32");
            this.sizeMethod = "computeFixed32Size";
            this.sizeDescriptor = "(II)I";
            this.writeMethod = "writeFixed32";
            this.writeDescriptor = "(II)V";
            this.readMethod = "readFixed32";
            this.readDescriptor = "()I";
        }
    }

    private static class ObjectFixed32Coder extends Fixed32Coder {
        private static final Coder INSTANCE = new ObjectFixed32Coder();

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
