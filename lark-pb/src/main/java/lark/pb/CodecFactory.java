package lark.pb;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cuigh
 */
public final class CodecFactory {
    private static final Map<String, Codec> CACHED = new ConcurrentHashMap<>();
    private static final Object LOCKER = new Object();
    private static final CodecClassLoader LOADER = new CodecClassLoader(Thread.currentThread().getContextClassLoader());

    private CodecFactory() {
        // 防止实例化
    }

    /**
     * 获取编解码器
     *
     * @param cls 目标类信息
     * @param <T> 数据类型
     * @return 编解码器
     */
    @SuppressWarnings("unchecked")
    public static <T> Codec<T> get(Class<T> cls) {
        Codec codec = CACHED.get(cls.getName());
        if (codec != null) {
            return codec;
        }

        synchronized (LOCKER) {
            if (cls.isPrimitive() || cls.isEnum() || cls.isAnnotation() || cls.isInterface()) {
                throw new IllegalArgumentException("can't create codec for type: " + cls.getName());
            }

            codec = CACHED.get(cls.getName());
            if (codec != null) {
                return codec;
            }

            String codecTypeName = CodecUtils.getCodecTypeName(cls);
            try {
                Class<?> codecClass = LOADER.loadCodecClass(codecTypeName, cls);
                codec = (Codec) codecClass.getDeclaredConstructor().newInstance();
                CACHED.put(cls.getName(), codec);
                return codec;
            } catch (Throwable e) {
                throw new ProtoException(e);
            }
        }
    }

    private static class CodecClassLoader extends ClassLoader {
        public CodecClassLoader(final ClassLoader parent) {
            super(parent);
        }

        public Class<?> loadCodecClass(final String qualifiedClassName, Class<?> cls) {
            byte[] bytes = CodecBuilder.build(cls);
            return defineClass(qualifiedClassName, bytes, 0, bytes.length);
        }
    }

}
