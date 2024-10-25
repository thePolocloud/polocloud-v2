package dev.httpmarco.polocloud.node.commands;

import dev.httpmarco.polocloud.api.groups.ClusterGroup;
import dev.httpmarco.polocloud.api.players.ClusterPlayer;
import dev.httpmarco.polocloud.api.services.ClusterService;
import dev.httpmarco.polocloud.node.cluster.NodeEndpoint;
import dev.httpmarco.polocloud.node.commands.specific.*;
import dev.httpmarco.polocloud.node.commands.type.*;
import dev.httpmarco.polocloud.node.modules.LoadedModule;
import dev.httpmarco.polocloud.node.platforms.Platform;
import dev.httpmarco.polocloud.node.platforms.PlatformVersion;
import dev.httpmarco.polocloud.node.templates.Template;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class CommandArgumentType {

    public @NotNull CommandArgument<Template> TemplateArgument(String key) {
        return new TemplateArgument(key);
    }

    public @NotNull CommandArgument<LoadedModule> ModuleArgument(String key) {
        return new ModuleArgument(key);
    }

    public @NotNull CommandArgument<ClusterGroup> ClusterGroup(String key) {
        return new GroupArgument(key);
    }

    public @NotNull CommandArgument<ClusterService> ClusterService(String key) {
        return new ServiceArgument(key);
    }

    public @NotNull CommandArgument<PlatformVersion> PlatformVersion(String key) {
        return new PlatformBindVersionArgument(key);
    }

    public @NotNull CommandArgument<ClusterPlayer> Player(String key) {
        return new PlayerArgument(key);
    }

    public @NotNull CommandArgument<Platform> Platform(String key) {
        return new PlatformArgument(key);
    }

    public @NotNull <E extends Enum<E>> CommandArgument<E> Enum(Class<E> enumClass, String key) {
        return new EnumArgument<>(enumClass, key);
    }

    public @NotNull CommandArgument<NodeEndpoint> NodeEndpoint(String key) {
        return new NodeEndpointArgument(key);
    }

    @Contract("_ -> new")
    public @NotNull CommandArgument<Integer> Integer(String key) {
        return new IntArgument(key);
    }

    @Contract("_ -> new")
    public @NotNull CommandArgument<String> StringArray(String key) {
        return new StringArrayArgument(key);
    }

    @Contract("_ -> new")
    public @NotNull CommandArgument<Boolean> Boolean(String key) {
        return new BooleanArgument(key);
    }


    @Contract("_ -> new")
    public @NotNull CommandArgument<String> Text(String key) {
        return new TextArgument(key);
    }

    @Contract("_ -> new")
    public @NotNull CommandArgument<String> Keyword(String key) {
        return new KeywordArgument(key);
    }
}
