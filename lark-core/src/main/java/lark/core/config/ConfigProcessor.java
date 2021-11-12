package lark.core.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;

/**
 * 全局配置和配置中心配置处理器
 *
 * @author cuigh
 */
public class ConfigProcessor implements EnvironmentPostProcessor {
    private static final String ENV_KEY = "LARK_GLOBAL_CONFIG_PATH";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        PropertySource<?> source = loadGlobalConfig();
        if (source != null) {
            environment.getPropertySources().addLast(source);
        }

        source = loadCenterConfig();
        if (source != null) {
            environment.getPropertySources().addLast(source);
        }
    }

    private MapPropertySource loadCenterConfig() {
        // todo:
        return new MapPropertySource("center", new HashMap<>());
    }

    private PropertySource<?> loadGlobalConfig() {
        String path = System.getenv(ENV_KEY);
        if (StringUtils.isEmpty(path)) {
            path = "/etc/lark/global.yml";
        }
        Resource res = new FileSystemResource(path);
        if (!res.exists()) {
            return null;
        }
        try {
            YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
            return loader.load("global", res).get(0);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load global configuration from " + res, e);
        }
    }
}
