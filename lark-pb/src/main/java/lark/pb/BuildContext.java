package lark.pb;

import lombok.Getter;
import org.objectweb.asm.Type;

/**
 * @author cuigh
 */
@Getter
public class BuildContext {
    private Class<?> beanClass;
    private String beanClassName;
    private String codecClassName;
    private String beanDescriptor;

    public BuildContext(Class<?> beanClass) {
        this.beanClass = beanClass;
        this.beanClassName = Type.getInternalName(beanClass);
        this.beanDescriptor = Type.getDescriptor(beanClass);
        this.codecClassName = CodecUtils.getCodecTypeName(beanClass).replace('.', '/');
    }
}
