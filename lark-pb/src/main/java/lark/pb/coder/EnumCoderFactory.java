package lark.pb.coder;

import lark.core.lang.EnumValuable;
import lark.pb.field.FieldInfo;
import lark.pb.field.FieldKind;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

/**
 * enum 代码生成器工厂类
 *
 * @author cuigh
 */
public class EnumCoderFactory implements CoderFactory {
    @Override
    public Coder get(Class<?> clazz) {
        if (EnumValuable.class.isAssignableFrom(clazz)) {
            return EnumValuableCoder.INSTANCE;
        } else if (Enum.class.isAssignableFrom(clazz)) {
            return EnumCoder.INSTANCE;
        }
        return null;
    }

    private static abstract class BaseEnumCoder extends SimpleCoder {
        private BaseEnumCoder(String realType) {
            super(realType, "Enum");
            this.sizeMethod = "computeEnumSize";
            this.sizeDescriptor = "(II)I";
            this.writeMethod = "writeEnum";
            this.writeDescriptor = "(II)V";
            this.readMethod = "readEnum";
            this.readDescriptor = "()I";
        }
    }

    private static class EnumCoder extends BaseEnumCoder {
        private static final Coder INSTANCE = new EnumCoder();

        private EnumCoder() {
            super("Enum");
        }

        @Override
        protected void convertTo(MethodVisitor mv, FieldInfo fi) {
            Class<?> clazz = fi.getKind() == FieldKind.LIST ? fi.getList().getItemClass() : fi.getField().getType();
            mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(clazz), "ordinal", "()I", false);
        }

        @Override
        protected void convertFrom(MethodVisitor mv, FieldInfo fi) {
            Class<?> clazz = fi.getKind() == FieldKind.LIST ? fi.getList().getItemClass() : fi.getField().getType();
            String type = Type.getInternalName(clazz);

            mv.visitMethodInsn(INVOKESTATIC, type, "values", String.format("()[L%s;", type), false);
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "to", "(I[Ljava/lang/Enum;)Ljava/lang/Enum;", false);
            mv.visitTypeInsn(CHECKCAST, type);
        }
    }

    private static class EnumValuableCoder extends BaseEnumCoder {
        private static final Coder INSTANCE = new EnumValuableCoder();

        private EnumValuableCoder() {
            super("EnumValuable");
        }

        @Override
        protected void convertTo(MethodVisitor mv, FieldInfo fi) {
            Class<?> clazz = fi.getKind() == FieldKind.LIST ? fi.getList().getItemClass() : fi.getField().getType();
            mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(clazz), "value", "()I", false);
        }

        @Override
        protected void convertFrom(MethodVisitor mv, FieldInfo fi) {
            Class<?> clazz = fi.getKind() == FieldKind.LIST ? fi.getList().getItemClass() : fi.getField().getType();
            String type = Type.getInternalName(clazz);
            mv.visitLdcInsn(Type.getType("L" + type + ";"));
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "to", "(ILjava/lang/Class;)Llark/core/lang/EnumValuable;", false);
            mv.visitTypeInsn(CHECKCAST, type);
        }
    }

    private static class DayOfWeekCoder extends BaseEnumCoder {
        private static final Coder INSTANCE = new DayOfWeekCoder();

        private DayOfWeekCoder() {
            super("DayOfWeek");
        }

        @Override
        protected void convertTo(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKEVIRTUAL, Types.DAY_OF_WEEK, "getValue", "()I", false);
        }

        @Override
        protected void convertFrom(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKESTATIC, Types.DAY_OF_WEEK, "of", "(I)Ljava/time/DayOfWeek;", false);
        }
    }
}
