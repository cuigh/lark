package lark.pb.coder;

import lark.pb.field.FieldInfo;
import org.objectweb.asm.MethodVisitor;

import java.math.BigDecimal;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

/**
 * string 代码生成器工厂类
 *
 * @author cuigh
 */
public class StringCoderFactory implements CoderFactory {
    @Override
    public Coder get(Class<?> clazz) {
        if (clazz == String.class) {
            return StringCoder.INSTANCE;
        } else if (clazz == BigDecimal.class) {
            return BigDecimalCoder.INSTANCE;
        }
        return null;
    }

    private static class StringCoder extends SimpleCoder {
        private static final Coder INSTANCE = new StringCoder();

        private StringCoder() {
            this("String");
        }

        private StringCoder(String realType) {
            super(realType, "String");
            this.sizeMethod = "computeStringSize";
            this.sizeDescriptor = "(ILjava/lang/String;)I";
            this.writeMethod = "writeString";
            this.writeDescriptor = "(ILjava/lang/String;)V";
            this.readMethod = "readString";
            this.readDescriptor = "()Ljava/lang/String;";
        }
    }

    private static class BigDecimalCoder extends StringCoder {
        private static final Coder INSTANCE = new BigDecimalCoder();

        private BigDecimalCoder() {
            super("BigDecimal");
        }

        @Override
        protected void convertTo(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "from", "(Ljava/math/BigDecimal;)Ljava/lang/String;", false);
        }

        @Override
        protected void convertFrom(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "to", "(Ljava/lang/String;)Ljava/math/BigDecimal;", false);
        }
    }
}
