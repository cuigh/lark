package lark.pb.coder;

/**
 * bytes 代码生成器工厂类
 *
 * @author cuigh
 */
public class BytesCoderFactory implements CoderFactory {
    @Override
    public Coder get(Class<?> clazz) {
        if (clazz == byte[].class) {
            return BytesCoder.INSTANCE;
        }
        return null;
    }

    private static class BytesCoder extends SimpleCoder {
        private static final Coder INSTANCE = new BytesCoder();

        private BytesCoder() {
            super("ByteArray", "ByteArray");
            this.sizeMethod = "computeByteArraySize";
            this.sizeDescriptor = "(I[B)I";
            this.writeMethod = "writeByteArray";
            this.writeDescriptor = "(I[B)V";
            this.readMethod = "readByteArray";
            this.readDescriptor = "()[B";
        }
    }
}
