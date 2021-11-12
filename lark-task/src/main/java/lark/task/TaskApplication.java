package lark.task;

import lark.net.rpc.RpcApplication;
import lark.task.service.ScheduleService;
import lark.task.service.TaskService;
import lark.task.service.TaskServiceImpl;
import org.apache.commons.logging.Log;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cuigh
 */
public class TaskApplication extends RpcApplication {
    public TaskApplication(Class<?>... primarySources) {
        this(null, primarySources);
    }

    public TaskApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
        super(resourceLoader, primarySources);
    }

    @Override
    protected void load() {
        super.load();

        if (server != null) {
            Log logger = getApplicationLog();
            Map<String, Executor> beans = ctx.getBeansOfType(Executor.class);
            logger.info(String.format("Found %d executors", beans.size()));

            Map<String, Executor> executors = new HashMap<>(beans.size());
            beans.forEach((n, executor) -> {
                Class<?> clazz = executor.getClass();
                Task task = clazz.getAnnotation(Task.class);
                String name = (task == null || StringUtils.isEmpty(task.name())) ? clazz.getSimpleName() : task.name();
                logger.info(String.format("Register executor: %s -> %s", name, executor.getClass().getName()));
                executors.put(name, executor);
            });

            ScheduleService scheduleService = ctx.getBean(ScheduleService.class);
            TaskService taskService = new TaskServiceImpl(scheduleService, executors);
            server.registerService(TaskService.class, taskService);
        }
    }
}
