package dev.httpmarco.polocloud.node.terminal.commands;

import dev.httpmarco.polocloud.node.commands.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
public final class GroupCommand extends Command {

    public GroupCommand() {
        super("group");


        var groupIdArgument = CommandArgumentType.Text("name");

        syntax(context -> {
            log.info("Successfully created &b{} &7group&8.", context.arg(groupIdArgument));
        }, "Create a new cluster group.", CommandArgumentType.Keyword("create"), groupIdArgument);
    }
}
