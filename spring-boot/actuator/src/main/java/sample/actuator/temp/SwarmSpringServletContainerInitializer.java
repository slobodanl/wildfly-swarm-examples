package sample.actuator.temp;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.jboss.msc.service.ServiceName;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Bob McWhirter
 */
public class SwarmSpringServletContainerInitializer implements WebApplicationInitializer {
    public static AtomicBoolean FIRED = new AtomicBoolean();

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        synchronized ( FIRED ) {
            if ( FIRED.getAndSet(true)) {
                System.err.println( "already fired" );
                return;
            }
        }

        System.err.println( "servlet-container-init");

        new Exception().printStackTrace();

        Map<String, ? extends ServletRegistration> registrations = servletContext.getServletRegistrations();

        for ( String name : registrations.keySet() ) {
            ServletRegistration reg = registrations.get(name);
            System.err.println( name + " >>>> " + reg.getName() + " // " + reg.getClassName() );
        }

        ServletContextInitializer init = (ServletContextInitializer) InnardsActivator.REGISTRY.getService(ServiceName.of( "swarm", "spring-boot", "init" ) ).getValue();

        init.onStartup( servletContext );

        registrations = servletContext.getServletRegistrations();

        for ( String name : registrations.keySet() ) {
            ServletRegistration reg = registrations.get(name);
            System.err.println( name + " >>>> " + reg.getName() + " // " + reg.getClassName() );
        }

        ApplicationContext rootContext = (ApplicationContext) servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        ApplicationContext cur = rootContext;

        while ( cur != null ) {
            System.err.println( "CONTEXT-----" );

            String[] names = cur.getBeanDefinitionNames();

            for (int i = 0; i < names.length; ++i) {
                System.err.println(names[i]);
            }
            cur = cur.getParent();
        }




    }

}
