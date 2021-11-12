package lark.db.mongo;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Component
@Aspect
public class MongoDataSourceAspect {
    private final static Logger LOGGER = LoggerFactory.getLogger(MongoDataSourceAspect.class);


    MongoDataSourceHolder mongoDataSourceHolder;

    public MongoDataSourceAspect(MongoDataSourceHolder mongoDataSourceHolder) {
        this.mongoDataSourceHolder = mongoDataSourceHolder;
    }

    @Pointcut("@annotation(lark.db.mongo.MongoDataSource)")
    public void switchDataSource() {

    }

    @Around("switchDataSource()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws IllegalAccessException {
        Object result = null;

        Object object = joinPoint.getTarget();
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature;
        Method method = methodSignature.getMethod();
        Annotation anno = method.getAnnotation(MongoDataSource.class);
        if (anno != null) {
            String name = ((MongoDataSource) anno).name();
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                Class clazz = field.getType();
                if (clazz == MongoTemplate.class) {
                    field.setAccessible(true);
                    field.set(object, mongoDataSourceHolder.getTemplate(name));
                }
            }
        }
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            LOGGER.error("执行dao失败", throwable);
        }
        return result;
    }
}
