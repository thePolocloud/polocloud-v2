package dev.httpmarco.polocloud.node.commands;

import com.google.inject.Singleton;
import dev.httpmarco.polocloud.node.Node;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Accessors(fluent = true)
@Singleton
public final class CommandServiceImpl implements CommandService {

    private final List<Command> commands = new ArrayList<>();

    @Contract(pure = true)
    @Override
    public @Unmodifiable List<Command> commandsByName(String name) {
        return commands.stream().filter(it -> it.name().equalsIgnoreCase(name) || Arrays.stream(it.aliases()).anyMatch(s -> s.equalsIgnoreCase(name))).toList();
    }

    @Override
    public void registerCommand(Command command) {
        Node.injector().injectMembers(command);
        this.commands.add(command);
    }

    @Override
    public void unregisterCommand(Command command) {
        this.commands.remove(command);
    }

    @Override
    public void call(String commandId, String[] args) {
        CommandSerializer.serializer(this, commandId, args);
    }
}
