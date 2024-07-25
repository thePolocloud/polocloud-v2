package dev.httpmarco.polocloud.node.commands.type;

import com.google.inject.Inject;
import dev.httpmarco.polocloud.node.Named;
import dev.httpmarco.polocloud.node.commands.CommandArgument;
import dev.httpmarco.polocloud.node.groups.ClusterGroup;
import dev.httpmarco.polocloud.node.groups.ClusterGroupImpl;
import dev.httpmarco.polocloud.node.groups.ClusterGroupService;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public final class GroupArgument extends CommandArgument<ClusterGroup> {

    @Inject
    private ClusterGroupService groupService;

    public GroupArgument(String key) {
        super(key);
    }

    @Override
    public boolean predication(@NotNull String rawInput) {
        return groupService.exists(rawInput);
    }

    @Contract(pure = true)
    @Override
    public @NotNull String wrongReason() {
        return "The Argument " + key() + " is not a registered cluster group!";
    }

    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull @Unmodifiable List<String> defaultArgs() {
        return groupService.groups().stream().map(Named::name).toList();
    }

    @Contract("_ -> new")
    @Override
    public @NotNull ClusterGroup buildResult(String input) {
        return new ClusterGroupImpl(input);
    }
}
