package dev.httpmarco.polocloud.node.services;

import dev.httpmarco.polocloud.api.groups.ClusterGroup;
import dev.httpmarco.polocloud.api.services.ClusterService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import java.util.UUID;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class ClusterServiceImpl implements ClusterService {

    private final ClusterGroup group;
    private final int orderedId;
    private final UUID id;
    private final int port;
    private final String hostname;
    private final String runningNode;

}
