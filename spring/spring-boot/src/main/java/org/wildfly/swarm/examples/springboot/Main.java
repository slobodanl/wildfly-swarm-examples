package org.wildfly.swarm.examples.springboot;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.undertow.WARArchive;

/**
 * @author Bob McWhirter
 */
public class Main {

    public static void main(String...args) throws Exception {

        Swarm swarm = new Swarm();
        swarm.start(true);

        WARArchive archive = ShrinkWrap.create( WARArchive.class, "spring-boot-servlet.war" );
        archive.addAllDependencies();
        archive.addPackage( Main.class.getPackage() );
        archive.addAsServiceProvider(ServletContextInitializer.class, SpringBootServletInitializer.class);

        swarm.deploy( archive );
    }
}
