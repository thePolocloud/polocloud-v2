package dev.httpmarco.polocloud.api.services;

import dev.httpmarco.polocloud.api.Named;
import dev.httpmarco.polocloud.api.groups.ClusterGroup;

import java.util.UUID;

public interface ClusterService extends Named {

    ClusterGroup group();

    int orderedId();

    UUID id();

    int port();

    String hostname();

    String runningNode();

    @Override
    default String name() {
        return group().name() + "-" + orderedId();
    }
}
