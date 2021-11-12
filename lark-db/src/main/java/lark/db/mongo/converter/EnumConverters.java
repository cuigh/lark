package lark.db.mongo.converter;

import lark.core.lang.EnumValuable;
import lark.core.util.Enums;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

public class EnumConverters {
    public static class EnumReadConverterFactory implements ConverterFactory<Integer, EnumValuable> {
        @Override
        public <T extends EnumValuable> Converter<Integer, T> getConverter(Class<T> targetType) {
            if (!targetType.isEnum()) {
                throw new IllegalArgumentException("The target type " + targetType.getName() + " does not refer to an enum");
            }
            return new EnumReadConverter(targetType);
        }
    }

    @WritingConverter
    public static class EnumWriteConverter implements Converter<Enum<?>, Object> {
        @Override
        public Object convert(Enum<?> source) {
            if (source instanceof EnumValuable) {
                return ((EnumValuable) (source)).value();
            } else {
                return source.name();
            }
        }
    }

    @ReadingConverter
    public static class EnumReadConverter<T extends Enum & EnumValuable> implements Converter<Integer, Enum> {
        private final Class<T> enumType;

        public EnumReadConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public Enum convert(Integer source) {
            return Enums.valueOf(enumType, source);
        }
    }
}
