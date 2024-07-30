package dev.httpmarco.polocloud.node.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class CommandBindArgument<T, B> extends CommandArgument<T> {

    public CommandBindArgument(String key) {
        super(key);
    }

    public abstract List<String> defaultArgs(B previousBind, String rawInput);



}
