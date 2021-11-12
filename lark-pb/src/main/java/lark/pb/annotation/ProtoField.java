package lark.pb.annotation;

import lark.pb.field.FieldType;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ProtoField {
    boolean required() default false;

    int order() default 0;

    FieldType type() default FieldType.AUTO;

//    FieldType keyType() default FieldType.AUTO;
//
//    FieldType valueType() default FieldType.AUTO;

    String description() default "";
}
