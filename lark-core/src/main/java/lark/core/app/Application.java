package lark.core.app;

import lark.core.app.registry.Collector;
import lark.core.app.registry.Registry;
import lark.core.data.Guid;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.io.ResourceLoader;

import java.util.*;

/**
 * @author cuigh
 */
public class Application extends SpringApplication {
    protected ConfigurableApplicationContext ctx;
    private String id = new Guid().toString();

    public Application(Class<?>... primarySources) {
        this(null, primarySources);
    }

    public Application(ResourceLoader resourceLoader, Class<?>... primarySources) {
        super(resourceLoader, primarySources);
        this.init();
//        super.addListeners(this::handleEvent);
    }

    /**
     * 获取程序启动时动态生成的唯一 ID
     *
     * @return 程序 ID
     */
    public String getId() {
        return id;
    }

    /**
     * 获取程序名称
     *
     * @return 程序名称
     */
    public String getName() {
        return ctx.getId();
    }

    /**
     * 获取程序版本
     *
     * @return 程序版本
     */
    public String getVersion() {
        return ctx.getEnvironment().getProperty("lark.application.version");
    }

    protected void init() {
        System.setProperty("java.net.preferIPv4Stack", "true");
        setBannerMode(Banner.Mode.OFF);
    }

    protected void load() {
        // 留给子类扩展
    }

    protected void start() {
        // 留给子类扩展
    }

    @Override
    protected void afterRefresh(ConfigurableApplicationContext ctx, ApplicationArguments args) {
        this.ctx = ctx;
        try {
            load();
            start();
            register();
        } catch (Exception e) {
            throw new FatalException(e);
        }
    }

    private void register() {
        Registry registry = findBean(Registry.class);
        if (registry == null) {
            return;
        }

        Map<String, Collector> collectors = ctx.getBeansOfType(Collector.class);
        registry.setInfo(this.getName(), this.getId(), () -> collectProperties(collectors));
        registry.start();

        ctx.addApplicationListener((ContextClosedEvent e) -> registry.stop());
    }

    private Map<String, Object> collectProperties(Map<String, Collector> collectors) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("version", getVersion());
        properties.put("start_time", ctx.getStartupDate());
        collectors.forEach((k, v) -> v.collect(properties));
        return properties;
    }

    /**
     * Find bean by type, return null if no bean was found
     *
     * @param type bean class
     * @param <T>  bean type
     * @return bean instance
     */
    public <T> T findBean(Class<T> type) {
        Map<String, T> beans = ctx.getBeansOfType(type);
        if (beans.isEmpty()) {
            return null;
        }

        Iterator<Map.Entry<String, T>> iterator = beans.entrySet().iterator();
        return iterator.next().getValue();
    }

    /**
     * Find beans by type
     *
     * @param type bean class
     * @param <T>  bean type
     * @return bean list
     */
    public <T> List<T> findBeans(Class<T> type) {
        Map<String, T> beans = ctx.getBeansOfType(type);
        List<T> list = new ArrayList<>(beans.size());
        beans.forEach((k, v) -> list.add(v));
        return list;
    }

//    private void handleEvent(ApplicationEvent event) {
//        // ApplicationStartingEvent, ApplicationEnvironmentPreparedEvent, ApplicationPreparedEvent,
//        // ApplicationStartedEvent, ApplicationReadyEvent, ApplicationFailedEvent
//        if (event instanceof ApplicationEnvironmentPreparedEvent) {
//            this.env = ((ApplicationEnvironmentPreparedEvent) event).getEnvironment();
//        } else if (event instanceof ApplicationStartedEvent) {
//            this.ctx = ((ApplicationStartedEvent) event).getApplicationContext();
//            this.logger = LoggerFactory.getLogger(Application.class);
//            this.load();
//        } else if (event instanceof ApplicationReadyEvent) {
//            this.start();
//        }
//    }
}
