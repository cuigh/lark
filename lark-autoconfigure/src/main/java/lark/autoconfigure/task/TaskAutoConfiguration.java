package lark.autoconfigure.task;

import lark.net.rpc.client.ServiceFactory;
import lark.task.TaskApplication;
import lark.task.service.ScheduleService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author cuigh
 */
@Configuration
@ConditionalOnClass(TaskApplication.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TaskAutoConfiguration {
    private static final String SERVER = "skynet";

    @Bean
    @ConditionalOnMissingBean
    public ScheduleService scheduleService(ServiceFactory serviceFactory) {
        return serviceFactory.get(SERVER, ScheduleService.class);
    }
}
