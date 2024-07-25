package dev.httpmarco.polocloud.node;

import com.google.inject.Inject;
import com.google.inject.Injector;
import dev.httpmarco.polocloud.node.cluster.ClusterService;
import dev.httpmarco.polocloud.node.commands.CommandService;
import dev.httpmarco.polocloud.node.platforms.PlatformService;
import dev.httpmarco.polocloud.node.terminal.commands.ClearCommand;
import dev.httpmarco.polocloud.node.terminal.commands.GroupCommand;
import dev.httpmarco.polocloud.node.terminal.commands.ShutdownCommand;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
@Accessors(fluent = true)
public final class Node {

    @Getter
    @Setter
    private static Injector injector;

    @Inject
    public void initializeCommand(@NotNull CommandService service) {
        service.registerCommand(new ShutdownCommand());
        service.registerCommand(new GroupCommand());
        service.registerCommand(new ClearCommand());
    }

    @Inject
    public void initializeCluster(ClusterService service) {
        service.initialize();
    }

    @Inject
    public void initializeCluster(PlatformService service) {
        service.initialize();
    }

    @Inject
    public void logStats() {
        log.info("Cluster node boot successfully &8(&7Took {}ms&8)", System.currentTimeMillis() - Long.parseLong(System.getProperty("startup")));
    }


    @Inject
    public void handleShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(NodeShutdown::run));
    }
}
