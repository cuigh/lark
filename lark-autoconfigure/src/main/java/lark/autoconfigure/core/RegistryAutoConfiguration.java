package lark.autoconfigure.core;

import lark.autoconfigure.etcd.Etcd2AutoConfiguration;
import lark.core.app.registry.EtcdRegistry;
import lark.core.app.registry.Registry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.zalando.boot.etcd.EtcdClient;

import java.time.Duration;

/**
 * @author cuigh
 */
@Configuration
@AutoConfigureAfter(Etcd2AutoConfiguration.class)
public class RegistryAutoConfiguration {
    @Bean
    @ConditionalOnBean({EtcdClient.class, EtcdRegistryProperties.class})
    @ConditionalOnMissingBean
    public Registry etcdRegistry(EtcdClient etcdClient, TaskScheduler scheduler, EtcdRegistryProperties properties) {
        EtcdRegistry registry = new EtcdRegistry(etcdClient, scheduler);
        registry.setInterval(properties.interval);
        return registry;
    }

    @EnableConfigurationProperties
    @ConditionalOnProperty(prefix = "lark.application.registry", name = "type", havingValue = "etcd", matchIfMissing = true)
    @ConfigurationProperties(prefix = "lark.application.registry.options")
    static class EtcdRegistryProperties {
        private Duration interval = Duration.ofSeconds(30);

        public void setInterval(Duration interval) {
            this.interval = interval;
        }
    }
}
