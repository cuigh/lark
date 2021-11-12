package lark.task.service;

import lark.core.codec.JsonCodec;
import lark.core.sync.ThreadPoolBuilder;
import lark.core.util.Exceptions;
import lark.task.Executor;
import lark.task.TaskContext;
import lark.task.data.ExecuteParam;
import lark.task.data.NotifyRequest;
import lark.task.data.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

/**
 * @author cuigh
 */
public class TaskServiceImpl implements TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);
    private ExecutorService pool = new ThreadPoolBuilder("Task-", 0, Integer.MAX_VALUE, 0).build();
    private ConcurrentMap<String, String> runningTasks = new ConcurrentHashMap<>();
    private ScheduleService scheduleService;
    private Map<String, lark.task.Executor> executors;

    public TaskServiceImpl(ScheduleService scheduleService, Map<String, lark.task.Executor> executors) {
        this.scheduleService = scheduleService;
        this.executors = executors;
    }

    /**
     * 执行任务
     *
     * @param param 任务参数
     * @return
     */
    @Override
    public Result execute(ExecuteParam param) {
        LOGGER.info("接收任务:{}", JsonCodec.encode(param));

        if (param.getType() == ExecuteParam.ExecuteType.AUTO && runningTasks.containsKey(param.getName())) {
            pool.execute(() -> {
                String error = String.format("任务 %s 正在执行, 跳过此次调度(如果多次发生类型情况, 请检查调度时间是否合理)", param.getName());
                LOGGER.warn(error);
                Date start = new Date();
                this.notify(param.getName(), param.getId(), newResult(error), start, new Date());
            });
            return newResult(null);
        }

        String name = StringUtils.isEmpty(param.getAlias()) ? param.getName() : param.getAlias();
        lark.task.Executor executor = executors.get(name);
        if (executor == null) {
            LOGGER.error("找不到任务:{}", name);
            return newResult("找不到任务: " + name);
        }

        try {
            if (param.getType() == ExecuteParam.ExecuteType.AUTO) {
                runningTasks.put(param.getName(), param.getAlias());
            }
            pool.execute(() -> this.execute(executor, param));
        } catch (Exception e) {
            LOGGER.error("提交任务到线程池失败", e);
            return newResult("提交任务到线程池失败: " + e.getMessage());
        }

        return newResult(null);
    }

    private void execute(Executor executor, ExecuteParam param) {
        Date start = new Date();
        try {
            LOGGER.info("开始执行任务:{}，执行方式:{}", param.getName(), param.getType());
            TaskContext ctx = new TaskContext(param);
            executor.execute(ctx);
            this.notify(param.getName(), param.getId(), newResult(null), start, new Date());
            LOGGER.info("任务执行成功, 耗时:{}", Duration.ofMillis(System.currentTimeMillis() - start.getTime()));
        } catch (Exception e) {
            String error = e.getMessage();
            if (StringUtils.isEmpty(error)) {
                error = e.toString();
            }
            this.notify(param.getName(), param.getId(), newResult(error), start, new Date());
            LOGGER.error("任务执行失败, 耗时:{}, 错误信息:{}", Duration.ofMillis(System.currentTimeMillis() - start.getTime()),
                    Exceptions.getStackTrace(e));
        } finally {
            if (param.getType() == ExecuteParam.ExecuteType.AUTO) {
                runningTasks.remove(param.getName());
            }
        }
    }

    /**
     * 通知 skynet 任务执行结果
     *
     * @param name   任务名称
     * @param id     任务ID
     * @param result 执行结果
     * @param start  任务执行开始时间
     * @param end    任务执行结束时间
     */
    private void notify(String name, String id, Result result, Date start, Date end) {
        try {
            NotifyRequest request = new NotifyRequest();
            request.setId(id);
            request.setName(name);
            request.setResult(result);
            request.setStartTime(start);
            request.setEndTime(end);

            Result nr = scheduleService.notify(request);
            if (!nr.isSuccess()) {
                LOGGER.error("通知任务状态错误, ID:{}, Name:{}, Error:{}", id, name, nr.getErrorInfo());
            }
        } catch (Exception e) {
            LOGGER.error("通知任务状态异常, ID:{}, Name:{}, Error:", id, name, e);
        }
    }

    private Result newResult(String error) {
        Result result = new Result();
        result.setSuccess(StringUtils.isEmpty(error));
        result.setErrorInfo(error);
        return result;
    }
}
