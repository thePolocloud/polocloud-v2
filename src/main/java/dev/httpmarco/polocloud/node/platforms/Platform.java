package dev.httpmarco.polocloud.node.platforms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Set;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public final class Platform {

    private String platform;
    private Set<PlatformEntry> versions;

}
