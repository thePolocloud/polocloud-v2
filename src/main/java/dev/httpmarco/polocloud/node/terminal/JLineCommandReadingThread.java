package dev.httpmarco.polocloud.node.terminal;

import dev.httpmarco.polocloud.node.NodeConfig;
import dev.httpmarco.polocloud.node.commands.CommandService;
import dev.httpmarco.polocloud.node.terminal.util.TerminalColorUtil;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;
import java.util.Arrays;

public final class JLineCommandReadingThread extends Thread {

    private final NodeConfig localNodeImpl;
    private final CommandService commandService;
    private final JLineTerminal terminal;


    public JLineCommandReadingThread(NodeConfig localNodeImpl, CommandService commandService, JLineTerminal terminal) {
        this.localNodeImpl = localNodeImpl;
        this.commandService = commandService;
        this.terminal = terminal;

        setContextClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public void run() {

        var prompt = TerminalColorUtil.replaceColorCodes("&9" + localNodeImpl.localNode().name() + "&8@&7cloud &8» &7");

        while (!isInterrupted()) {
            try {
                try {
                    var rawLine = terminal.lineReader().readLine(prompt);

                    if (rawLine.isEmpty()) {
                        continue;
                    }

                    final var line = rawLine.split(" ");

                    if (line.length > 0) {
                        commandService.call(line[0], Arrays.copyOfRange(line, 1, line.length));
                    }
                } catch (EndOfFileException ignore) {
                }
            } catch (UserInterruptException exception) {
                System.exit(-1);
            }
        }
    }
}
