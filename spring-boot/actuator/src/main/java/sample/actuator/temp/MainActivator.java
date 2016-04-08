package sample.actuator.temp;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.jboss.msc.service.ServiceActivator;
import org.jboss.msc.service.ServiceActivatorContext;
import org.jboss.msc.service.ServiceRegistry;
import org.jboss.msc.service.ServiceRegistryException;
import org.jboss.msc.service.ServiceTarget;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.support.SpringFactoriesLoader;
import sample.actuator.SampleActuatorApplication;

/**
 * @author Bob McWhirter
 */
public class MainActivator implements ServiceActivator {

    public static ServiceRegistry REGISTRY;

    public static ServiceTarget TARGET;

    @Override
    public void activate(ServiceActivatorContext context) throws ServiceRegistryException {
        REGISTRY = context.getServiceRegistry();
        TARGET = context.getServiceTarget();

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        Thread thread = new Thread(() -> {
            System.err.println("running");
            try {
                SpringApplication.run(new Object[]{
                                EmbeddedSwarm.class,
                                SampleActuatorApplication.class},
                        new String[]{});
            } catch (Throwable t) {
                t.printStackTrace();
            }
            System.err.println("running complete");
        });

        thread.setContextClassLoader(cl);

        thread.start();
    }

}
