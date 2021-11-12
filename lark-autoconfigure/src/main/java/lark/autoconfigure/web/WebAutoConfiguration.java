package lark.autoconfigure.web;

import lark.core.app.registry.Collector;
import lark.core.util.Networks;
import lark.core.util.Strings;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;
import java.util.Map;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring MVC.
 *
 * @author cuigh
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebAutoConfiguration {
    @Bean
    public Collector webCollector(Environment env) {
        return properties -> {
            String address = env.getProperty("server.address");
            if (Strings.isEmpty(address)) {
                String prefix = env.getProperty("server.address-prefix");
                address = Networks.findLocalIP4(prefix) + ":" + env.getProperty("server.port");
            }
            Map<String, String> options = Collections.singletonMap("address", address);
            properties.put("web", options);
        };
    }

    @Configuration
    public static class WebConfigurer implements WebMvcConfigurer {
    }
}
