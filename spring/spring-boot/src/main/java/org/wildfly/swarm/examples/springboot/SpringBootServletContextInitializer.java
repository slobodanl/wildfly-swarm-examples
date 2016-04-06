package org.wildfly.swarm.examples.springboot;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.context.embedded.ServletContextInitializer;

/**
 * @author Bob McWhirter
 */
public class SpringBootServletContextInitializer implements ServletContextInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.err.println( "** ON STARTUP" );
    }
}
