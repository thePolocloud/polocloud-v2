package dev.httpmarco.polocloud.node.commands.type;

import dev.httpmarco.polocloud.node.commands.CommandArgument;
import dev.httpmarco.polocloud.node.commands.CommandBindArgument;
import dev.httpmarco.polocloud.node.platforms.Platform;
import dev.httpmarco.polocloud.node.platforms.PlatformService;
import dev.httpmarco.polocloud.node.platforms.PlatformVersion;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public final class PlatformVersionArgument extends CommandBindArgument<PlatformVersion, PlatformVersion> {

    private final PlatformService platformService;

    public PlatformVersionArgument(String key, PlatformService platformService) {
        super(key);
        this.platformService = platformService;
    }

    @Override
    public boolean predication(@NotNull String rawInput) {
        //todo
        return true;
    }

    @Contract(pure = true)
    @Override
    public PlatformVersion buildResult(String input) {
        //todo
        return new PlatformVersion("1.21", null, null);
    }

    @Override
    public List<String> defaultArgs(PlatformVersion previousBind, String rawInput) {
        return List.of();
    }
}
