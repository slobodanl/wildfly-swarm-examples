package sample.actuator.temp;

import java.util.Arrays;

import org.jboss.msc.service.ServiceActivator;
import org.jboss.msc.service.ServiceActivatorContext;
import org.jboss.msc.service.ServiceRegistry;
import org.jboss.msc.service.ServiceRegistryException;
import org.jboss.msc.service.ServiceTarget;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
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

        new Thread(() -> {
            try {
                ConfigurableApplicationContext ctx = SpringApplication.run(SampleActuatorApplication.class);

                String[] beanNames = ctx.getBeanDefinitionNames();
                Arrays.sort(beanNames);
                for (String beanName : beanNames) {
                    System.err.println(beanName);
                    Object bean = ctx.getBean(beanName);
                    System.err.println( "  " + bean + "  " + bean.getClass().getClassLoader() );
                }

            } catch (Throwable t) {
                t.printStackTrace();
            }
        }).start();
    }

}
