package dev.httpmarco.polocloud.node.groups;

import java.util.Set;

public interface ClusterGroupService {

    Set<ClusterGroup> groups();

    boolean exists(String group);

}
