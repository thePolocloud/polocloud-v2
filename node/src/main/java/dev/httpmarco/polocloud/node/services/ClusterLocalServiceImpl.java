package dev.httpmarco.polocloud.node.services;

import dev.httpmarco.polocloud.api.groups.ClusterGroup;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
public final class ClusterLocalServiceImpl extends ClusterServiceImpl {

    private Process process;

    public ClusterLocalServiceImpl(ClusterGroup group, int orderedId, UUID id, int port, String hostname, String runningNode) {
        super(group, orderedId, id, port, hostname, runningNode);
    }
}
