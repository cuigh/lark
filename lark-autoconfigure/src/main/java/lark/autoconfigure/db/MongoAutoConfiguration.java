package lark.autoconfigure.db;

import com.mongodb.MongoClient;
import lark.db.mongo.MongoDataSourceAspect;
import lark.db.mongo.MongoDataSourceHolder;
import lark.db.mongo.converter.EnumConverters;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for MongoDB.
 *
 * @author cuigh
 */
@Configuration
@ConditionalOnClass({MongoClient.class, MongoTemplate.class})
public class MongoAutoConfiguration {
    @SuppressWarnings("unchecked")
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDbFactory factory, MongoMappingContext context, MongoCustomConversions conversions) {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new EnumConverters.EnumWriteConverter());
        // this is a dummy registration , actually it's a work-around because
        // spring-mongodb does'nt has the option to register converter factory.
        // so we register the converter that our factory uses.
        converters.add(new EnumConverters.EnumReadConverter(null));

        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
//        mappingConverter.setCustomConversions(conversions);
        mappingConverter.setCustomConversions(new MongoCustomConversions(converters));
        mappingConverter.afterPropertiesSet();
        GenericConversionService conversionService = (GenericConversionService) mappingConverter.getConversionService();
        conversionService.addConverterFactory(new EnumConverters.EnumReadConverterFactory());
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null, context));
        mappingConverter.afterPropertiesSet();
        return mappingConverter;
    }

    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "lark.data")
    public static class MongoDataSourceProperties {
        private Map<String, MongoProperties> mongodb;

        public void setMongodb(Map<String, MongoProperties> mongodb) {
            this.mongodb = mongodb;
        }

    }

    @Bean
    @ConditionalOnMissingBean
    public MongoDataSourceHolder mongoDataSourceHolder(MongoDataSourceProperties mongoDataSourceProperties) {
        MongoDataSourceHolder mongoDataSourceHolder = new MongoDataSourceHolder();
        if (mongoDataSourceProperties.mongodb != null) {
            mongoDataSourceProperties.mongodb.forEach(mongoDataSourceHolder::addProperty);
        }

        return  mongoDataSourceHolder;
    }

    @Bean
    public MongoDataSourceAspect mongoDataSourceAspect(MongoDataSourceHolder mongoDataSourceHolder) {
        return new MongoDataSourceAspect(mongoDataSourceHolder);
    }
}
