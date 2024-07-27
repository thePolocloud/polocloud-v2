package dev.httpmarco.polocloud.api.groups;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface ClusterGroupService {

    Set<ClusterGroup> groups();

    boolean exists(String group);

    CompletableFuture<Optional<String>> delete(String group);

}
