package dev.httpmarco.polocloud.node.terminal;

import dev.httpmarco.polocloud.node.commands.CommandService;
import dev.httpmarco.polocloud.node.commands.SyntaxCommand;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.Arrays;
import java.util.List;

@Log4j2
@AllArgsConstructor
public final class JLineTerminalCompleter implements Completer {

    private CommandService commandService;

    @Override
    public void complete(LineReader lineReader, @NotNull ParsedLine parsedLine, List<Candidate> list) {
        if (parsedLine.wordIndex() == 0) {
            // we only display the command names -> not aliases

            for (var command : commandService.commands()) {
                // if one command start with the given command first word
                if (command.name().startsWith(parsedLine.word())) {
                    list.add(new Candidate(command.name()));
                }

                Arrays.stream(command.aliases())
                        .filter(it -> it.startsWith(parsedLine.word()))
                        .forEach(alias -> list.add(new Candidate(alias)));
            }
            return;
        }

        var commandName = parsedLine.words().get(0);
        for (var command : commandService.commandsByName(commandName)) {

            for (SyntaxCommand syntaxCommand : command.syntaxCommands) {

                if (isMatchingSyntax(parsedLine, syntaxCommand)) {
                    addSuggestions(parsedLine, syntaxCommand, list);
                }
            }
        }
    }

    private boolean isMatchingSyntax(ParsedLine parsedLine, SyntaxCommand syntaxCommand) {
        var argumentIndex = parsedLine.wordIndex() - 1;
        if (argumentIndex >= syntaxCommand.arguments().length) {
            return false;
        }

        for (int i = 0; i < argumentIndex; i++) {
            var expectedArgument = syntaxCommand.arguments()[i];
            var enteredArgument = parsedLine.words().get(i + 1).replace("<", "").replace(">", "");
            if (!expectedArgument.key().equals(enteredArgument) && !expectedArgument.defaultArgs().contains(enteredArgument)) {
                return false;
            }
        }
        return true;
    }

    private void addSuggestions(ParsedLine parsedLine, SyntaxCommand syntaxCommand, List<Candidate> list) {
        var argumentIndex = parsedLine.wordIndex() - 1;
        if (argumentIndex >= syntaxCommand.arguments().length) {
            return;
        }

        var argument = syntaxCommand.arguments()[argumentIndex];

        if (argument.defaultArgs().isEmpty()) {
            String candidateValue = "<" + argument.key() + ">";
            if (list.stream().noneMatch(candidate -> candidate.value().equals(candidateValue))) {
                list.add(new Candidate(candidateValue));
            }
        } else {
            argument.defaultArgs().stream()
                    .filter(defaultArg -> list.stream().noneMatch(candidate -> candidate.value().equals(defaultArg)))
                    .forEach(defaultArg -> list.add(new Candidate(defaultArg)));
        }
    }
}