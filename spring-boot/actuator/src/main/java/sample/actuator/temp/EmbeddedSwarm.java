package sample.actuator.temp;

import javax.servlet.ServletContainerInitializer;

import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ValueService;
import org.jboss.msc.value.ImmediateValue;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerException;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.SpringServletContainerInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.wildfly.swarm.container.internal.Deployer;
import org.wildfly.swarm.msc.ServiceActivatorArchive;
import org.wildfly.swarm.undertow.WARArchive;
import sample.actuator.SampleActuatorApplication;

/**
 * @author Bob McWhirter
 */
@Configuration
public class EmbeddedSwarm {

    @Bean
    public EmbeddedServletContainerFactory embeddedServletContainerFactory() {
        System.err.println("get embedded servlet container factory");
        return new EmbeddedServletContainerFactory() {
            @Override
            public EmbeddedServletContainer getEmbeddedServletContainer(ServletContextInitializer... initializers) {
                System.err.println("get embedded servlet container");
                for (ServletContextInitializer each : initializers) {
                    MainActivator.TARGET.addService(ServiceName.of("swarm", "spring-boot", "init"),
                            new ValueService<>(new ImmediateValue<>(each)))
                            .install();
                    System.err.println("each: " + each + " // " + each.getClass().getClassLoader());
                }

                WARArchive archive = ShrinkWrap.create(WARArchive.class, "spring-boot-app.war");
                try {
                    archive.addClass(InnardsActivator.class.getName());
                    archive.addClass(SwarmSpringServletContainerInitializer.class.getName());
                    archive.as(ServiceActivatorArchive.class).addServiceActivator(InnardsActivator.class.getName());

                    archive.addAsServiceProvider(ServletContainerInitializer.class.getName(), SpringServletContainerInitializer.class.getName());
                    //archive.addAsServiceProvider(WebApplicationInitializer.class.getName(), SwarmSpringServletContainerInitializer.class.getName());

                    //archive.addAsServiceProvider(ServletContextInitializer.class.getName(), SpringServletContainerInitializer.class.getName());
                    //archive.addClass(SwarmSpringServletContainerInitializer.class.getName());
                    archive.addPackage(SampleActuatorApplication.class.getPackage());
                    archive.addModule("swarm.application", "spring");


                    //archive.addAllDependencies();
                    //archive.addModule( "javax.servlet.api" );

                    System.err.println("Deployer CL: " + Deployer.class.getClassLoader());
                    //archive.addAllDependencies();
                    Deployer deployer = (Deployer) MainActivator.REGISTRY.getService(ServiceName.of("swarm", "deployer")).getValue();
                    deployer.deploy(archive);

                    //deployer.deploy(archive);
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
