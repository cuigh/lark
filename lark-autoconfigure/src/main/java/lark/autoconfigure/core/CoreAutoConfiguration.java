package lark.autoconfigure.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author cuigh
 */
@Configuration
public class CoreAutoConfiguration {
    /**
     * @return 线程池
     */
    @Bean
    @Lazy
    public TaskScheduler larkTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("scheduler-");
        return scheduler;
    }
}
