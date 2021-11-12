package lark.pb.coder;

import lark.pb.field.FieldInfo;
import org.objectweb.asm.MethodVisitor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * int64 代码生成器工厂类
 *
 * @author cuigh
 */
public class Int64CoderFactory implements CoderFactory {
    @Override
    public Coder get(Class<?> clazz) {
        if (clazz == long.class) {
            return Int64Coder.INSTANCE;
        } else if (clazz == Long.class) {
            return ObjectInt64Coder.INSTANCE;
        } else if (clazz == Date.class) {
            return DateCoder.INSTANCE;
        } else if (clazz == LocalDateTime.class) {
            return LocalDateTimeCoder.INSTANCE;
        } else if (clazz == LocalDate.class) {
            return LocalDateCoder.INSTANCE;
        } else if (clazz == LocalTime.class) {
            return LocalTimeCoder.INSTANCE;
        } else if (clazz == ZonedDateTime.class) {
            return ZonedDateTimeCoder.INSTANCE;
        }
        return null;
    }

    private static class Int64Coder extends SimpleCoder {
        private static final Coder INSTANCE = new Int64Coder();

        private Int64Coder() {
            this("Int64");
        }

        private Int64Coder(String realType) {
            super(realType, "Int64");
            this.sizeMethod = "computeInt64Size";
            this.sizeDescriptor = "(IJ)I";
            this.writeMethod = "writeInt64";
            this.writeDescriptor = "(IJ)V";
            this.readMethod = "readInt64";
            this.readDescriptor = "()J";
        }
    }

    private static class ObjectInt64Coder extends Int64Coder {
        private static final Coder INSTANCE = new ObjectInt64Coder();

        @Override
        protected void convertTo(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKEVIRTUAL, Types.LONG, "longValue", "()J", false);
        }

        @Override
        protected void convertFrom(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKESTATIC, Types.LONG, "valueOf", "(J)Ljava/lang/Long;", false);
        }
    }

    private static class DateCoder extends Int64Coder {
        private static final Coder INSTANCE = new DateCoder();

        private DateCoder() {
            super("Date");
        }

        @Override
        protected void convertTo(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKEVIRTUAL, Types.DATE, "getTime", "()J", false);
        }

        @Override
        protected void convertFrom(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "to", "(J)Ljava/util/Date;", false);
        }
    }

    private static class LocalDateTimeCoder extends Int64Coder {
        private static final Coder INSTANCE = new LocalDateTimeCoder();

        private LocalDateTimeCoder() {
            super("LocalDateTime");
        }

        @Override
        protected void convertTo(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "from", "(Ljava/time/LocalDateTime;)J", false);
        }

        @Override
        protected void convertFrom(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "to", "(J)Ljava/time/LocalDateTime;", false);
        }
    }

    private static class LocalDateCoder extends Int64Coder {
        private static final Coder INSTANCE = new LocalDateCoder();

        private LocalDateCoder() {
            super("LocalDate");
        }

        @Override
        protected void convertTo(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "from", "(Ljava/time/LocalDate;)J", false);
        }

        @Override
        protected void convertFrom(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "to", "(J)Ljava/time/LocalDate;", false);
        }
    }

    private static class LocalTimeCoder extends Int64Coder {
        private static final Coder INSTANCE = new LocalTimeCoder();

        private LocalTimeCoder() {
            super("LocalTime");
        }

        @Override
        protected void convertTo(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "from", "(Ljava/time/LocalTime;)J", false);
        }

        @Override
        protected void convertFrom(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "to", "(J)Ljava/time/LocalTime;", false);
        }
    }

    private static class ZonedDateTimeCoder extends Int64Coder {
        private static final Coder INSTANCE = new ZonedDateTimeCoder();

        private ZonedDateTimeCoder() {
            super("ZonedDateTime");
        }

        @Override
        protected void convertTo(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "from", "(Ljava/time/ZonedDateTime;)J", false);
        }

        @Override
        protected void convertFrom(MethodVisitor mv, FieldInfo fi) {
            mv.visitMethodInsn(INVOKESTATIC, getHelperType(), "to", "(J)Ljava/time/ZonedDateTime;", false);
        }
    }
}
