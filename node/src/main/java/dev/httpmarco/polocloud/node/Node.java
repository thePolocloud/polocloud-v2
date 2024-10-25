package dev.httpmarco.polocloud.node;

import dev.httpmarco.polocloud.api.CloudAPI;
import dev.httpmarco.polocloud.api.event.EventProvider;
import dev.httpmarco.polocloud.api.groups.GroupProperties;
import dev.httpmarco.polocloud.api.properties.PropertiesPool;
import dev.httpmarco.polocloud.node.cluster.ClusterProviderImpl;
import dev.httpmarco.polocloud.node.commands.CommandService;
import dev.httpmarco.polocloud.node.commands.CommandServiceImpl;
import dev.httpmarco.polocloud.node.events.EventProviderImpl;
import dev.httpmarco.polocloud.node.groups.ClusterGroupProviderImpl;
import dev.httpmarco.polocloud.node.modules.ModuleProvider;
import dev.httpmarco.polocloud.node.platforms.PlatformService;
import dev.httpmarco.polocloud.node.players.ClusterPlayerProviderImpl;
import dev.httpmarco.polocloud.api.properties.PropertyRegister;
import dev.httpmarco.polocloud.node.screens.ScreenProvider;
import dev.httpmarco.polocloud.node.services.ClusterServiceProviderImpl;
import dev.httpmarco.polocloud.node.templates.TemplatesProvider;
import dev.httpmarco.polocloud.node.terminal.JLineTerminal;
import dev.httpmarco.polocloud.node.terminal.commands.ClusterCommand;
import dev.httpmarco.polocloud.node.terminal.commands.GroupCommand;
import dev.httpmarco.polocloud.node.terminal.commands.ServiceCommand;
import dev.httpmarco.polocloud.node.update.AutoUpdater;
import dev.httpmarco.polocloud.node.util.Configurations;
import dev.httpmarco.polocloud.node.util.StringUtils;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Log4j2
@Getter
@Accessors(fluent = true)
public final class Node extends CloudAPI {

    @Getter
    private static Node instance;

    private final NodeConfig nodeConfig;
    private final ClusterProviderImpl clusterProvider;
    private final TemplatesProvider templatesProvider;
    private final EventProvider eventProvider;
    private final PlatformService platformService;
    private final ClusterGroupProviderImpl groupProvider;
    private final ClusterServiceProviderImpl serviceProvider;
    private final ClusterPlayerProviderImpl playerProvider;
    private final ScreenProvider screenProvider;
    private final ModuleProvider moduleProvider;
    private final AutoUpdater autoUpdater;

    // only all properties of this local cluster node instance
    private final PropertiesPool nodeProperties;

    private final JLineTerminal terminal;
    private final CommandService commandService;

    public Node() {
        instance = this;

        //register all local node properties
        PropertyRegister.register(
                NodeProperties.PROXY_PORT_START_RANGE,
                NodeProperties.SERVICE_PORT_START_RANGE,
                NodeProperties.SERVER_PORT_START_RANGE,
                GroupProperties.MAINTENANCE,
                GroupProperties.PERCENTAGE_TO_START_NEW_SERVER
        );

        this.nodeConfig = Configurations.readContent(Path.of("config.json"), new NodeConfig());
        this.nodeProperties = nodeConfig.propertiesPool();
        this.templatesProvider = new TemplatesProvider();
        this.clusterProvider = new ClusterProviderImpl(nodeConfig);
        this.eventProvider = new EventProviderImpl();
        this.moduleProvider = new ModuleProvider();

        this.commandService = new CommandServiceImpl();
        this.terminal = new JLineTerminal(nodeConfig);

        this.platformService = new PlatformService();
        this.groupProvider = new ClusterGroupProviderImpl(clusterProvider);
        this.serviceProvider = new ClusterServiceProviderImpl();
        this.playerProvider = new ClusterPlayerProviderImpl();
        this.screenProvider = new ScreenProvider();

        this.autoUpdater = new AutoUpdater();
        if (nodeConfig.checkForUpdates()) {
            startUpdateLoop();
        }

        // register provider commands
        this.commandService.registerCommands(new GroupCommand(), new ServiceCommand(), new ClusterCommand(this.clusterProvider));

        // start cluster and check other node
        this.clusterProvider.initialize();

        // set cluster proxy token or sync with head
        this.serviceProvider.serviceProxyToken(StringUtils.randomString(8));

        // load all Modules
        this.moduleProvider.loadAllUnloadedModules();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> NodeShutdown.nodeShutdownTotal(false)));

        log.info("Cluster node boot successfully &8(&7Took {}ms&8)", System.currentTimeMillis() - Long.parseLong(System.getProperty("startup")));

        this.terminal.allowInput();
        this.serviceProvider.clusterServiceQueue().start();
    }

    public void updateNodeConfig() {
        Configurations.writeContent(Path.of("config.json"), this.nodeConfig);
    }

    private void startUpdateLoop() {
        CompletableFuture.runAsync(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    this.autoUpdater.notifyIfUpdateAvailable();
                    Thread.sleep(TimeUnit.HOURS.toMillis(1));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }
}
