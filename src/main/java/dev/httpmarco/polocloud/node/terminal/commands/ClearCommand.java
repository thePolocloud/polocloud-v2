package dev.httpmarco.polocloud.node.terminal.commands;

import com.google.inject.Inject;
import dev.httpmarco.polocloud.node.commands.Command;
import dev.httpmarco.polocloud.node.terminal.JLineTerminal;

public final class ClearCommand extends Command {

    @Inject
    private JLineTerminal terminal;

    public ClearCommand() {
        super("clear", "cc");

        defaultExecution(commandContext -> terminal.clear());
    }
}
