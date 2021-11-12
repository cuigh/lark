package lark.net.rpc.client;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;

/**
 * @author cuigh
 */
public class ServiceClassLoader extends ClassLoader {
    ServiceClassLoader(final ClassLoader parent) {
        super(parent);
    }

    private static byte[] build(Class<?> clazz) {
        BuildContext ctx = new BuildContext(clazz);
        ClassWriter cw = createClassWriter(ctx);
        buildConstructor(cw, clazz);
        buildMethods(cw, ctx);
        cw.visitEnd();
        return cw.toByteArray();
    }

    private static ClassWriter createClassWriter(BuildContext ctx) {
        ClassWriter cw = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);
        String superName = Type.getInternalName(BaseService.class);
        String[] interfaces = new String[]{ctx.ifaceClassName};
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, ctx.implClassName, null, superName, interfaces);
        return cw;
    }

    private static void buildConstructor(ClassWriter cw, Class<?> cls) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Llark/net/rpc/client/Client;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitLdcInsn(Type.getType(Type.getDescriptor(cls)));
        mv.visitMethodInsn(INVOKESPECIAL, "lark/net/rpc/client/BaseService", "<init>", "(Llark/net/rpc/client/Client;Ljava/lang/Class;)V", false);
        mv.visitInsn(RETURN);
//        mv.visitMaxs(3, 2);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

//    private static String getConstructorDescriptor(Class<?> cls, Class<?>... parameterTypes) {
//        Constructor<?> constructor = cls.getConstructor(parameterTypes);
//        return Type.getConstructorDescriptor(constructor);
//    }

    private static String getInvokeDescriptor() {
        Method[] methods = BaseService.class.getDeclaredMethods();
        for (Method m : methods) {
            if ("invoke".equals(m.getName())) {
                return Type.getMethodDescriptor(m);
            }
        }
        return null;
    }

    private static void buildMethods(ClassWriter cw, BuildContext ctx) {
        String descriptor = getInvokeDescriptor();
        Method[] methods = ctx.ifaceClass.getMethods();
        for (Method m : methods) {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, m.getName(), Type.getMethodDescriptor(m), null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn(m.getName());

            int paramCount = m.getParameterCount();
            if (paramCount == 0) {
                mv.visitInsn(ACONST_NULL);
            } else {
                if (paramCount < 6) {
                    mv.visitInsn(ICONST_0 + paramCount);
                } else {
                    mv.visitIntInsn(BIPUSH, paramCount);
                }
                mv.visitTypeInsn(ANEWARRAY, Types.OBJECT);
                for (int i = 0; i < paramCount; i++) {
                    mv.visitInsn(DUP);

                    if (i < 6) {
                        mv.visitInsn(ICONST_0 + i);
                    } else {
                        mv.visitIntInsn(BIPUSH, i);
                    }

                    Class<?> parameterType = m.getParameterTypes()[i];
                    if (parameterType.isPrimitive()) {
                        buildPrimitiveParameter(mv, parameterType, i);
                    } else {
                        mv.visitVarInsn(ALOAD, i + 1);
                    }

                    mv.visitInsn(AASTORE);
                }
            }
            mv.visitMethodInsn(INVOKEVIRTUAL, ctx.implClassName, "invoke", descriptor, false);
            if (m.getReturnType() == void.class) {
                mv.visitInsn(POP);
                mv.visitInsn(RETURN);
            } else if (m.getReturnType().isPrimitive()) {
                Class<?> wrapClass = getWrapClass(m.getReturnType());
                mv.visitTypeInsn(CHECKCAST, Type.getInternalName(wrapClass));
                mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(wrapClass), m.getReturnType().getSimpleName() + "Value", "()" + Type.getDescriptor(m.getReturnType()), false);
                mv.visitInsn(IRETURN);
            } else {
                mv.visitTypeInsn(CHECKCAST, Type.getInternalName(m.getReturnType()));
                mv.visitInsn(ARETURN);
            }

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
    }

    static String getProxyClassName(Class<?> cls) {
        return cls.getName() + "$$Proxy";
    }

    private static void buildPrimitiveParameter(MethodVisitor mv, Class<?> cls, int index) {
        int value = index + 1;
        if (index > 5) {
            mv.visitIntInsn(BIPUSH, index);
        }
        if (cls == boolean.class) {
            mv.visitVarInsn(ILOAD, value);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        } else if (cls == byte.class) {
            mv.visitVarInsn(ILOAD, value);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
        } else if (cls == char.class) {
            mv.visitVarInsn(ILOAD, value);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
        } else if (cls == short.class) {
            mv.visitVarInsn(ILOAD, value);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
        } else if (cls == int.class) {
            mv.visitVarInsn(ILOAD, value);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        } else if (cls == long.class) {
            mv.visitVarInsn(LLOAD, value);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        } else if (cls == float.class) {
            mv.visitVarInsn(FLOAD, value);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
        } else if (cls == double.class) {
            mv.visitVarInsn(DLOAD, value);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
        }
    }

    private static Class<?> getWrapClass(Class<?> cls) {
        if (cls == boolean.class) {
            return Boolean.class;
        } else if (cls == byte.class) {
            return Byte.class;
        } else if (cls == char.class) {
            return Character.class;
        } else if (cls == short.class) {
            return Short.class;
        } else if (cls == int.class) {
            return Integer.class;
        } else if (cls == long.class) {
            return Long.class;
        } else if (cls == float.class) {
            return Float.class;
        } else if (cls == double.class) {
            return Double.class;
        }
        return cls;
    }

    Class<?> loadProxyClass(final String qualifiedClassName, Class<?> cls) {
        byte[] bytes = build(cls);
        return defineClass(qualifiedClassName, bytes, 0, bytes.length);
    }

    private static class Types {
        private static final String OBJECT = Type.getInternalName(Object.class);
        private static final String BASE_SERVICE = Type.getInternalName(BaseService.class);
    }

    private static class Descriptors {
        private static final String CLASS = Type.getDescriptor(Class.class);
    }

    private static class BuildContext {
        private Class<?> ifaceClass;
        private String ifaceClassName;
        private String implClassName;
//        private String ifaceDescriptor;

        private BuildContext(Class<?> cls) {
            this.ifaceClass = cls;
            this.ifaceClassName = Type.getInternalName(cls);
//            this.ifaceDescriptor = Type.getDescriptor(cls);
            this.implClassName = Type.getInternalName(cls) + "$$Proxy";
        }
    }
}
