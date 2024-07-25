package dev.httpmarco.polocloud.node.groups;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Getter
@Singleton
@Accessors(fluent = true)
public final class ClusterGroupServiceImpl implements ClusterGroupService {

    private final Set<ClusterGroup> groups = new HashSet<>();

    @Override
    public boolean exists(String group) {
        return this.groups.stream().anyMatch(it -> it.name().equalsIgnoreCase(group));
    }
}
