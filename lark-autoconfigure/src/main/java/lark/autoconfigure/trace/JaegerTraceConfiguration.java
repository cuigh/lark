package lark.autoconfigure.trace;

import io.opentracing.Tracer;
import io.opentracing.contrib.java.spring.jaeger.starter.JaegerAutoConfiguration;
import lark.net.rpc.client.ClientFilter;
import lark.net.rpc.client.ServiceFactory;
import lark.net.rpc.server.ServerFilter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author niantian
 */
@Configuration
@AutoConfigureAfter(JaegerAutoConfiguration.class)
@ConditionalOnClass(value = {Tracer.class, ServiceFactory.class})
public class JaegerTraceConfiguration {
    @Bean
    public ClientFilter clientFilter(Tracer tracer, ServiceFactory factory) {
        ClientFilter filter = handler -> new lark.net.rpc.client.filter.TraceHandler(handler, tracer);
        factory.use(filter);
        return filter;
    }

    @Bean
    public ServerFilter serverFilter(Tracer tracer) {
        return handler -> new lark.net.rpc.server.filter.TraceHandler(tracer, handler);
    }
}