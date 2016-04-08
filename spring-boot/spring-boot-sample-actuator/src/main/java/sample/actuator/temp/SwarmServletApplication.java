package sample.actuator.temp;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ValueService;
import org.jboss.msc.value.ImmediateValue;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerException;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.SpringServletContainerInitializer;
import org.wildfly.swarm.container.DeploymentException;
import org.wildfly.swarm.container.internal.Deployer;
import org.wildfly.swarm.msc.ServiceActivatorArchive;
import org.wildfly.swarm.undertow.WARArchive;
import sample.actuator.SampleActuatorApplication;

/**
 * @author Bob McWhirter
 */
@Configuration
public class SwarmServletApplication {

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
                    System.err.println("each: " + each + " // " + each.getClass().getClassLoader() );
                }
                return new EmbeddedServletContainer() {
                    @Override
                    public void start() throws EmbeddedServletContainerException {

                        System.err.println("START: " + getClass().getClassLoader());

                        WARArchive archive = ShrinkWrap.create(WARArchive.class, "spring-boot-app.war");
                        try {
                            archive.as(ServiceActivatorArchive.class).addServiceActivator( InnardsActivator.class );
                            archive.addAsServiceProvider(ServletContainerInitializer.class, SwarmSpringServletContainerInitializer.class);
                            archive.addClass(SwarmSpringServletContainerInitializer.class);
                            archive.addPackage(SampleActuatorApplication.class.getPackage());
                            archive.addAllDependencies();
                            Deployer deployer = (Deployer) MainActivator.REGISTRY.getService(ServiceName.of("swarm", "deployer")).getValue();
                            deployer.deploy(archive);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
