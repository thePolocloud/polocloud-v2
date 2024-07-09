/*
 * Copyright 2024 Mirco Lindenau | HttpMarco
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.httpmarco.polocloud.base;

import dev.httpmarco.osgan.files.OsganFile;
import dev.httpmarco.osgan.networking.server.CommunicationServer;
import dev.httpmarco.osgan.utils.data.Pair;
import dev.httpmarco.polocloud.api.CloudAPI;
import dev.httpmarco.polocloud.api.dependencies.Dependency;
import dev.httpmarco.polocloud.api.node.NodeService;
import dev.httpmarco.polocloud.api.player.CloudPlayerProvider;
import dev.httpmarco.polocloud.api.properties.PropertyPool;
import dev.httpmarco.polocloud.api.services.CloudServiceProvider;
import dev.httpmarco.polocloud.base.common.PropertiesPoolSerializer;
import dev.httpmarco.polocloud.base.configuration.CloudConfiguration;
import dev.httpmarco.polocloud.base.events.GlobalEventNode;
import dev.httpmarco.polocloud.base.groups.CloudGroupProvider;
import dev.httpmarco.polocloud.base.logging.FileLoggerHandler;
import dev.httpmarco.polocloud.base.logging.LoggerOutPutStream;
import dev.httpmarco.polocloud.base.module.ModuleManager;
import dev.httpmarco.polocloud.base.node.CloudNodeService;
import dev.httpmarco.polocloud.base.node.LocalNode;
import dev.httpmarco.polocloud.base.player.CloudPlayerProviderImpl;
import dev.httpmarco.polocloud.base.services.CloudServiceProviderImpl;
import dev.httpmarco.polocloud.base.templates.TemplatesService;
import dev.httpmarco.polocloud.base.terminal.CloudTerminal;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

@Getter
@Accessors(fluent = true)
public final class CloudBase extends CloudAPI {

    private final PropertyPool globalProperties;

    private final CloudTerminal terminal;
    private final NodeService nodeService;
    private final CloudGroupProvider groupProvider;
    private final CloudServiceProvider serviceProvider;
    private final TemplatesService templatesService;
    private final CloudPlayerProvider playerProvider;
    private final CloudConfiguration cloudConfiguration = OsganFile.define("config.json").asDocument(new CloudConfiguration(), new Pair<>(PropertyPool.class, new PropertiesPoolSerializer())).content();
    private final GlobalEventNode globalEventNode;
    private final ModuleManager moduleManager;

    private boolean running = true;

    public CloudBase() {
        Dependency.load("com.google.code.gson", "gson", "2.10.1");
        Dependency.load("org.jline", "jline", "3.26.1");
        Dependency.load("org.fusesource.jansi", "jansi", "2.4.1");
        Dependency.load("commons-io", "commons-io", "2.16.1");
        Dependency.load("com.electronwill.night-config", "toml", "3.6.7");
        Dependency.load("com.electronwill.night-config", "core", "3.6.7");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown(true)));

        this.globalProperties = this.cloudConfiguration.properties();

        this.terminal = new CloudTerminal();
        // register logging layers (for general output)
        this.loggerFactory().registerLoggers(new FileLoggerHandler(), terminal);

        System.setErr(new PrintStream(new LoggerOutPutStream(true), true, StandardCharsets.UTF_8));
        System.setOut(new PrintStream(new LoggerOutPutStream(), true, StandardCharsets.UTF_8));
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> e.printStackTrace());

        this.nodeService = new CloudNodeService(new LocalNode(cloudConfiguration.clusterId(), cloudConfiguration.clusterName(), "127.0.0.1", 8192), cloudConfiguration.externalNodes());
        // print cloud header information

        terminal.spacer();
        terminal.spacer("   &3PoloCloud &2- &1Simple minecraft cloudsystem &2- &1v1.0.10-snapshot");
        terminal.spacer("   &1node&2: &1" + nodeService.localNode().name() + " &2| &1id&2: &1" + nodeService.localNode().id());
        terminal.spacer();

        this.nodeService.localNode().initialize();

        this.globalEventNode = new GlobalEventNode();
        this.groupProvider = new CloudGroupProvider();
        this.templatesService = new TemplatesService();
        this.serviceProvider = new CloudServiceProviderImpl();
        this.playerProvider = new CloudPlayerProviderImpl();
        (this.moduleManager = new ModuleManager()).loadAllUnloadedModules();

        this.moduleManager.getLoadedModules().forEach(module -> {
            logger().info("Loaded module: " + module.metadata().name() + " by " + module.metadata().author());
        });

        logger().success("Successfully started up&2! (&1Took " + (System.currentTimeMillis() - Long.parseLong(System.getProperty("startup"))) + "ms&2)");

        this.terminal.start();
    }

    public void shutdown(boolean shutdownCycle) {
        if (!running) {
            return;
        }
        running = false;

        logger().info("Shutdown cloud...");
        ((CloudServiceProviderImpl) serviceProvider).close();

        for (var service : this.serviceProvider.services()) {
            service.shutdown();
        }

        this.nodeService.localNode().close();
        this.moduleManager.unloadAllModules();

        logger().info("Cloud successfully stopped!");
        this.loggerFactory().close();

        if (!shutdownCycle) {
            System.exit(0);
        }
    }

    public CommunicationServer transmitter() {
        return ((LocalNode) this.nodeService.localNode()).server();
    }

    public static CloudBase instance() {
        return (CloudBase) CloudAPI.instance();
    }
}
