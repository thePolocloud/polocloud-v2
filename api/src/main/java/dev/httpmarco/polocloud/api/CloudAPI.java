package dev.httpmarco.polocloud.api;

import dev.httpmarco.polocloud.api.event.EventProvider;
import dev.httpmarco.polocloud.api.groups.ClusterGroupProvider;
import dev.httpmarco.polocloud.api.modules.ModuleProvider;
import dev.httpmarco.polocloud.api.players.ClusterPlayerProvider;
import dev.httpmarco.polocloud.api.services.ClusterServiceProvider;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public abstract class CloudAPI {

    @Getter
    private static CloudAPI instance;

    public CloudAPI() {
        instance = this;
    }

    public abstract ClusterServiceProvider serviceProvider();

    public abstract ClusterGroupProvider groupProvider();

    public abstract EventProvider eventProvider();

    public abstract ClusterPlayerProvider playerProvider();

    public abstract ModuleProvider moduleProvider();
}
