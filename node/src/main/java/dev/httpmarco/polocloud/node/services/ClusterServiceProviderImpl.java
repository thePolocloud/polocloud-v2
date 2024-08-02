package dev.httpmarco.polocloud.node.services;

import dev.httpmarco.polocloud.api.services.ClusterService;
import dev.httpmarco.polocloud.api.services.ClusterServiceFactory;
import dev.httpmarco.polocloud.api.services.ClusterServiceProvider;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
@Accessors(fluent = true)
public final class ClusterServiceProviderImpl extends ClusterServiceProvider {

    private final ClusterServiceFactory factory = new ClusterServiceFactoryImpl();

    @Override
    public CompletableFuture<List<ClusterService>> servicesAsync() {
        return null;
    }
}
