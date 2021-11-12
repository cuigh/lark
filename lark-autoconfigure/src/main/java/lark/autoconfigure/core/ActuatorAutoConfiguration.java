package lark.autoconfigure.core;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author cuigh
 */
@ConditionalOnClass(Endpoint.class)
@Configuration
public class ActuatorAutoConfiguration {
    @Component
    @Endpoint(id = "jars")
    class JarsEndpoint {
        private static final String PREFIX = "BOOT-INF/lib/";

        @ReadOperation
        public List<String> list() throws IOException {
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            URL[] urls = ((URLClassLoader) cl).getURLs();
            if (urls.length != 1) {
                return Collections.emptyList();
            }

            List<String> jars = new ArrayList<>();
            try (JarFile file = new JarFile(urls[0].getFile())) {
                Enumeration<JarEntry> entries = file.entries();
                while (entries.hasMoreElements()) {
                    String name = entries.nextElement().getName();
                    if (name.startsWith(PREFIX)) {
                        jars.add(name.substring(PREFIX.length()));
                    }
                }
            }
            return jars;
        }
    }
}
