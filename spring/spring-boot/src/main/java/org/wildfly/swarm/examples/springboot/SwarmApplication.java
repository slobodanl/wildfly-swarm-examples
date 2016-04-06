package org.wildfly.swarm.examples.springboot;

import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerException;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Bob McWhirter
 */
@Configuration
public class SwarmApplication {

    @Bean
    public EmbeddedServletContainerFactory embeddedServletContainerFactory() {
        return new EmbeddedServletContainerFactory() {
            @Override
            public EmbeddedServletContainer getEmbeddedServletContainer(ServletContextInitializer... initializers) {
                return new EmbeddedServletContainer() {
                    @Override
                    public void start() throws EmbeddedServletContainerException {

                    }

                    @Override
                    public void stop() throws EmbeddedServletContainerException {

                    }

                    @Override
                    public int getPort() {
                        return 0;
                    }
                };
            }
        };
    }
}
