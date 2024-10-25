package dev.httpmarco.polocloud.node.commands.specific;

import dev.httpmarco.polocloud.node.Node;
import dev.httpmarco.polocloud.node.commands.CommandArgument;
import dev.httpmarco.polocloud.node.commands.CommandContext;
import dev.httpmarco.polocloud.api.modules.LoadedModule;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public final class ModuleArgument extends CommandArgument<LoadedModule> {

    public ModuleArgument(String key) {
        super(key);
    }

    @Override
    public boolean predication(@NotNull String rawInput) {
        return Node.instance().moduleProvider().loadedModules().stream().anyMatch(it -> it.metadata().id().startsWith(rawInput));
    }

    @Contract(pure = true)
    @Override
    public @NotNull String wrongReason() {
        return "The Argument " + key() + " is not a registered module id!";
    }

    @Override
    public @NotNull @Unmodifiable List<String> defaultArgs(CommandContext context) {
        return Node.instance().moduleProvider().loadedModules().stream().map(it -> it.metadata().id()).toList();
    }

    @Contract("_ -> new")
    @Override
    public LoadedModule buildResult(String input) {
        return Node.instance().moduleProvider().loadedModules().stream().filter(it -> it.metadata().id().equals(input)).findFirst().orElse(null);
    }
}