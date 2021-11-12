package lark.web;

import lark.core.app.Application;
import org.springframework.core.io.ResourceLoader;

/**
 * @author cuigh
 */
public class WebApplication extends Application {
    public WebApplication(Class<?>... primarySources) {
        this(null, primarySources);
    }

    public WebApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
        super(resourceLoader, primarySources);
    }
}
