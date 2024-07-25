package dev.httpmarco.polocloud.node.groups;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class ClusterGroupImpl implements ClusterGroup {

    private final String name;

}
