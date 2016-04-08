package sample.actuator.temp;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.wildfly.swarm.Swarm;
//import org.wildfly.swarm.logging.LoggingFraction;
import org.wildfly.swarm.msc.ServiceActivatorArchive;
import org.wildfly.swarm.spi.api.JARArchive;

/**
 * @author Bob McWhirter
 */
public class SwarmSpringApplication {

    public static void run(Object source, String... args) throws Exception {

        Swarm swarm = new Swarm();
        //swarm.fraction(LoggingFraction.createDebugLoggingFraction());
        swarm.start(true);

        JARArchive archive = ShrinkWrap.create(JARArchive.class, "spring-boot-bootstrap.jar");

        //archive.addModule( "swarm.application", "spring" );
        //archive.addPackage(SampleActuatorApplication.class.getPackage() );
        //archive.addModule( "org.jboss.shrinkwrap" );
        //archive.addModule( "javax.servlet.api" );

        String structure = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<jboss-deployment-structure>\n" +
                "    <deployment>\n" +
                "        <dependencies>\n" +
                "            <module name=\"swarm.application\" slot=\"spring\" services=\"import\">\n" +
                "                <imports>\n" +
                "                  <include path=\"**\"/>\n" +
                "                </imports>\n" +
                "            </module>\n" +
                "        </dependencies>\n" +
                "    </deployment>\n" +
                "</jboss-deployment-structure>";

        archive.addAsResource( new StringAsset(structure), "META-INF/jboss-deployment-structure.xml" );

        archive.as(ServiceActivatorArchive.class).addServiceActivator( MainActivator.class );
        archive.addPackage(MainActivator.class.getPackage());

        swarm.deploy( archive );


    }
}
