package lark.autoconfigure.etcd;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.zalando.boot.etcd.EtcdClient;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for etcd client v2.
 *
 * @author cuigh
 */
@Configuration
@ConditionalOnProperty(prefix = "etcd", name = "enabled", matchIfMissing = true)
@ConditionalOnClass(EtcdClient.class)
@EnableConfigurationProperties(EtcdProperties.class)
public class Etcd2AutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public EtcdClient etcdClient2(EtcdProperties props) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(3000);

        String[] addresses = props.getAddress().split(",");
        EtcdClient client = new EtcdClient(addresses);
        client.setRetryCount(0);
        client.setRetryDuration(0);
        client.setLocationUpdaterEnabled(true);
        client.setRequestFactory(requestFactory);
        return client;
    }
}