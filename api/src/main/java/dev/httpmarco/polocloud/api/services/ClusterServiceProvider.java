package dev.httpmarco.polocloud.api.services;

import lombok.SneakyThrows;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public abstract class ClusterServiceProvider {

    @SneakyThrows
    public List<ClusterService> services() {
        return servicesAsync().get(5, TimeUnit.SECONDS);
    }

    public abstract CompletableFuture<List<ClusterService>> servicesAsync();

    public abstract ClusterServiceFactory factory();

}
