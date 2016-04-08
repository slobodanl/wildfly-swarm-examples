package sample.actuator.temp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.jboss.msc.service.ServiceName;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Bob McWhirter
 */
public class SwarmSpringServletContainerInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.err.println( "servlet-container-init");

        Map<String, ? extends ServletRegistration> registrations = servletContext.getServletRegistrations();

        for ( String name : registrations.keySet() ) {
            ServletRegistration reg = registrations.get(name);
            System.err.println( name + " >>>> " + reg.getName() + " // " + reg.getClassName() );
        }

        Object init = InnardsActivator.REGISTRY.getService(ServiceName.of( "swarm", "spring-boot", "init" ) ).getValue();

        Class cur = init.getClass();

        Method onStartup = null;

        while ( cur != null ) {
            Method[] methods = init.getClass().getDeclaredMethods();
            for ( int i = 0 ; i < methods.length ; ++i ) {
                if ( methods[i].getName().equals( "onStartup" ) ) {
                    onStartup = methods[i];
                    break;
                }
            }

            cur = cur.getSuperclass();
        }

        if ( onStartup != null ) {
            onStartup.setAccessible(true);
            try {
                System.err.println( "INVOKE START" );
                onStartup.invoke( init, servletContext );
                System.err.println( "INVOKE END" );
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        registrations = servletContext.getServletRegistrations();

        for ( String name : registrations.keySet() ) {
            ServletRegistration reg = registrations.get(name);
            System.err.println( name + " >>>> " + reg.getName() + " // " + reg.getClassName() );
        }

        ApplicationContext rootContext = (ApplicationContext) servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        System.err.println( "ROOT: " + rootContext );


    }
}
