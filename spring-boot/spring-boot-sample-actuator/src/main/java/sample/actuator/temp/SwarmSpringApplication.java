package sample.actuator.temp;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.logging.LoggingFraction;
import org.wildfly.swarm.msc.ServiceActivatorArchive;
import org.wildfly.swarm.spi.api.JARArchive;
import sample.actuator.SampleActuatorApplication;

/**
 * @author Bob McWhirter
 */
public class SwarmSpringApplication {

    public static void run(Object source, String... args) throws Exception {

        Swarm swarm = new Swarm();
        //swarm.fraction(LoggingFraction.createDebugLoggingFraction());
        swarm.start();

        JARArchive archive = ShrinkWrap.create(JARArchive.class, "spring-boot-bootstrap.jar");

        archive.as(ServiceActivatorArchive.class).addServiceActivator( MainActivator.class );
        archive.addPackage(SampleActuatorApplication.class.getPackage() );
        archive.addModule( "swarm.application" );

        swarm.deploy( archive );

        /*
        LOG = System.err;
        try {
            Swarm swarm = new Swarm();
            swarm.start();

            Module module = Module.getBootModuleLoader().loadModule(ModuleIdentifier.create( "swarm.application" ) );
            Thread.currentThread().setContextClassLoader( module.getClassLoader() );
            System.err.println( module.getClassLoader().loadClass(EnableAutoConfigurationImportSelector.class.getName()) );
            System.err.println( "AAAA" );
            SpringApplication.run( new Object[] {
                    SwarmServletApplication.class,
                    source,
            }, args );
            System.err.println( "BBB" );
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
        */

    }
}
