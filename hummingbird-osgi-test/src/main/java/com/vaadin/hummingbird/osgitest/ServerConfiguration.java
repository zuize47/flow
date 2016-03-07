/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.hummingbird.osgitest;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.File;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.ConfigurationManager;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.karaf.options.KarafDistributionBaseConfigurationOption;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.options.MavenArtifactProvisionOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;

public class ServerConfiguration {

    public static String karafVersion() {
        ConfigurationManager cm = new ConfigurationManager();
        String karafVersion = cm.getProperty("pax.exam.karaf.version", "4.0.4");
        return karafVersion;
    }

    @Configuration
    public Option[] configuration() {

        MavenArtifactUrlReference karafUrl = CoreOptions.maven()
                .groupId("org.apache.karaf").artifactId("apache-karaf")
                .version(karafVersion()).type("zip");

        MavenUrlReference karafStandardFeature = CoreOptions.maven()
                .groupId("org.apache.karaf.features").artifactId("standard")
                .version(karafVersion()).classifier("features").type("xml");

        KarafDistributionBaseConfigurationOption karaf = KarafDistributionOption
                .karafDistributionConfiguration().frameworkUrl(karafUrl)
                .unpackDirectory(new File("target", "exam"))
                .useDeployFolder(false);

        MavenArtifactProvisionOption hummingbirdServer = mavenBundle(
                "com.vaadin", "hummingbird-server").version("0.0.2-SNAPSHOT");
        MavenArtifactProvisionOption hummingbirdPush = mavenBundle("com.vaadin",
                "hummingbird-push").version("0.0.2-SNAPSHOT");
        MavenArtifactProvisionOption hummingbirdClient = mavenBundle(
                "com.vaadin", "hummingbird-client").version("0.0.2-SNAPSHOT");
        MavenArtifactProvisionOption elementalJson = mavenBundle(
                "com.vaadin.external.gwt", "gwt-elemental-json")
                        .version("2.8.0.snapshot20160216");
        MavenArtifactProvisionOption elemental = mavenBundle("com.google.gwt",
                "gwt-elemental").version("2.8.0.snapshot20160216");
        MavenArtifactProvisionOption jsoup = mavenBundle("org.jsoup", "jsoup")
                .version("1.8.3");
        MavenArtifactProvisionOption vaadinSlf4j = mavenBundle(
                "com.vaadin.external.slf4j", "vaadin-slf4j-jdk14", "1.6.1");
        MavenArtifactProvisionOption atmosphere = mavenBundle(
                "com.vaadin.external.atmosphere", "atmosphere-runtime",
                "2.2.7.vaadin1");
        return options(
                KarafDistributionOption.features(karafStandardFeature,
                        "webconsole", "war", "web"),
                //
                hummingbirdClient, atmosphere, elementalJson, hummingbirdServer,
                hummingbirdPush, vaadinSlf4j, jsoup,
                //

                karaf
        // KarafDistributionOption.features(karafStandardFeature, "web")
        // CoreOptions.frameworkProperty("osgi.console").value("6666"),
        // CoreOptions.systemProperty("org.osgi.service.http.port")
        // .value("8181")

        );

        // hummingbird
        //
        // , CoreOptions.junitBundles()
        // mavenBundle("org.ops4j.pax.web", "pax-web-spi")
        // .version("2.0.2"),
        // mavenBundle("org.ops4j.pax.web", "pax-web-api")
        // .version("2.0.2"),
        // mavenBundle("org.ops4j.pax.web",
        // "pax-web-extender-war")
        // .version("2.0.2"),
        // mavenBundle("org.ops4j.pax.web",
        // "pax-web-extender-whiteboard")
        // .version("2.0.2"),
        // mavenBundle("org.ops4j.pax.web", "pax-web-jetty")
        // .version("2.0.2"),
        // mavenBundle("org.ops4j.pax.web", "pax-web-runtime")
        // .version("2.0.2"),
        // mavenBundle("org.ops4j.pax.web", "pax-web-jsp")
        // .version("2.0.2"),
        // mavenBundle("org.eclipse.jdt.core.compiler", "ecj")
        // .version("3.5.1"),
        // mavenBundle("org.eclipse.jetty", "jetty-util")
        // .version("8.1.4.v20120524"),
        // mavenBundle("org.eclipse.jetty", "jetty-io")
        // .version("8.1.4.v20120524"),
        // mavenBundle("org.eclipse.jetty", "jetty-http")
        // .version("8.1.4.v20120524"),
        // mavenBundle("org.eclipse.jetty",
        // "jetty-continuation")
        // .version("8.1.4.v20120524"),
        // mavenBundle("org.eclipse.jetty", "jetty-server")
        // .version("8.1.4.v20120524"),
        // mavenBundle("org.eclipse.jetty", "jetty-security")
        // .version("8.1.4.v20120524"),
        // mavenBundle("org.eclipse.jetty", "jetty-xml")
        // .version("8.1.4.v20120524"),
        // mavenBundle("org.eclipse.jetty", "jetty-servlet")
        // .version("8.1.4.v20120524"),
        // mavenBundle("org.apache.geronimo.specs",
        // "geronimo-servlet_3.0_spec").version("1.0"),
        // mavenBundle("org.osgi", "org.osgi.compendium",
        // "4.3.0")

        // mavenBundle("org.slf4j", "slf4j-api").versionAsInProject(),
        // mavenBundle("org.slf4j", "slf4j-simple").versionAsInProject()
        // .noStart(),
        //
        // mavenBundle("org.apache.geronimo.samples.osgi", "wab-sample",
        // "3.0.0")

        // );
    }
}
