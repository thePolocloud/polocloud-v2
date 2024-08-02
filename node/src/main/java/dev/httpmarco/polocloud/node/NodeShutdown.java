package dev.httpmarco.polocloud.node;

import dev.httpmarco.polocloud.node.cluster.ClusterService;
import dev.httpmarco.polocloud.node.cluster.NodeSituation;
import dev.httpmarco.polocloud.node.terminal.JLineTerminal;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
@UtilityClass
public final class NodeShutdown {

    public static void nodeShutdown() {

        var clusterService = Node.instance().clusterService();

        if (clusterService.localNode().situation().isStopping()) {
            return;
        }

        clusterService.localNode().situation(NodeSituation.STOPPING);

        clusterService.close();

        Node.instance().terminal().close();

        clusterService.localNode().situation(NodeSituation.STOPPED);
        System.exit(0);
    }
}
