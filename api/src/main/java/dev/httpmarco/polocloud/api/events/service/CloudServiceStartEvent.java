package dev.httpmarco.polocloud.api.events.service;

import dev.httpmarco.polocloud.api.services.CloudService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public final class CloudServiceStartEvent implements ServiceEvent {

    private final CloudService cloudService;
}
