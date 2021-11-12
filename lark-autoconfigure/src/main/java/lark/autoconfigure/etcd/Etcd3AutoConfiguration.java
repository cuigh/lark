package lark.autoconfigure.etcd;

import io.etcd.jetcd.Client;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for etcd client v3.
 *
 * @author cuigh
 */
@Configuration
@ConditionalOnProperty(prefix = "etcd", name = "enabled", matchIfMissing = true)
@ConditionalOnClass(Client.class)
@EnableConfigurationProperties(EtcdProperties.class)
public class Etcd3AutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public Client etcdClient3(EtcdProperties props) {
        String[] addresses = props.getAddress().split(",");
        return Client.builder().lazyInitialization(true).endpoints(addresses).build();
    }
}