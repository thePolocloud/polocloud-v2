package dev.httpmarco.polocloud.node;

import com.google.inject.Inject;
import dev.httpmarco.polocloud.node.cluster.ClusterService;
import dev.httpmarco.polocloud.node.cluster.NodeSituation;
import dev.httpmarco.polocloud.node.terminal.JLineTerminal;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
public final class NodeShutdown {

    @Inject
    public NodeShutdown(@NotNull JLineTerminal terminal, ClusterService clusterService) {

        if (clusterService.localNode().situation().isStopping()) {
            return;
        }

        clusterService.localNode().situation(NodeSituation.STOPPING);

        clusterService.close();

        terminal.close();

        clusterService.localNode().situation(NodeSituation.STOPPED);
        System.exit(0);
    }

    public static void run() {
        Node.injector().getInstance(NodeShutdown.class);
    }
}
