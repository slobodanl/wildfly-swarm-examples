/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.actuator;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.wildfly.swarm.arquillian.WithMain;
import org.wildfly.swarm.spi.api.JARArchive;
import org.wildfly.swarm.springboot.SpringApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests for endpoints configuration.
 *
 * @author Dave Syer
 */
@RunWith(Arquillian.class)
@WithMain(EndpointsPropertiesSampleActuatorApplicationTests.class)
//@ActiveProfiles("endpoints")
public class EndpointsPropertiesSampleActuatorApplicationTests {

    @Autowired
    private SecurityProperties security;

    @Value("${local.server.port}")
    private int port;

    public static void main(String...args) throws Exception {
        System.setProperty("spring.profiles.active", "endpoints" );
        SpringApplication.run( SampleActuatorApplication.class );
    }

    @Deployment
    public static Archive createDeployment() {
        JARArchive archive = ShrinkWrap.create(JARArchive.class);

        archive.as(ExplodedImporter.class)
                .importDirectory(new File("./target/classes"));

        archive.as(ExplodedImporter.class)
                .importDirectory(new File("./target/test-classes"));

        return archive;
    }

    @Test
    public void testCustomErrorPath() throws Exception {
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = new TestRestTemplate("user", getPassword())
                .getForEntity("http://localhost:" + this.port + "/oops", Map.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = entity.getBody();
        assertEquals("None", body.get("error"));
        assertEquals(999, body.get("status"));
    }

    @Test
    public void testCustomContextPath() throws Exception {
        ResponseEntity<String> entity = new TestRestTemplate("user", getPassword())
                .getForEntity("http://localhost:" + this.port + "/admin/health",
                        String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertTrue("Wrong body: " + entity.getBody(),
                entity.getBody().contains("\"status\":\"UP\""));
        System.err.println(entity.getBody());
        assertTrue("Wrong body: " + entity.getBody(),
                entity.getBody().contains("\"hello\":\"world\""));
    }

    private String getPassword() {
        System.err.println("this.security: " + this.security);
        return this.security.getUser().getPassword();
    }

}
