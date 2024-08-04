/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.command;

import me.finn.unlegitlibrary.command.events.*;
import me.finn.unlegitlibrary.event.EventManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {

    private final List<Command> commands;
    private final EventManager eventManager;

    public CommandManager(EventManager eventManager) {
        this.commands = new ArrayList<>();
        this.eventManager = eventManager;
    }

    public final void registerCommand(Command command) {
        if (this.commands.contains(command)) return;
        this.commands.add(command);
    }

    public final void unregisterCommand(Command command) {
        if (!this.commands.contains(command)) return;
        this.commands.remove(command);
    }

    public List<Command> getCommands() {
        return new ArrayList<>(commands);
    }

    public Command getCommandByName(String name) {
        for (Command command : commands) if (command.name.equals(name)) return command;
        return null;
    }

    public Command getCommandByAliases(List<String> aliases) {
        for (String alias : aliases) {
            for (Command command : commands) {
                List<String> aliasesCommand = new ArrayList<>();
                for (String registeredAlias : command.getAliases()) aliasesCommand.add(registeredAlias.toLowerCase());
                if (aliasesCommand.contains(alias.toLowerCase())) return command;
            }
        }

        return null;
    }

    public Command getCommandByAlias(String alias) {
        for (Command command : commands) {
            List<String> aliasesCommand = new ArrayList<>();
            for (String registeredAlias : command.getAliases()) aliasesCommand.add(registeredAlias.toLowerCase());
            if (aliasesCommand.contains(alias.toLowerCase())) return command;
        }

        return null;
    }

    public Command getCommand(String input) {
        if (getCommandByName(input) != null) return getCommandByName(input);
        if (getCommandByAlias(input) != null) return getCommandByAlias(input);
        return null;
    }

    public void execute(CommandExecutor commandExecutor, String line) {
        String[] split = line.split(" ");
        String command = split[0];

        String[] args = Arrays.copyOfRange(split, 1, split.length);

        if (getCommand(command) != null) {
            Command cmd = getCommand(command);
            PreCommandExecuteEvent preEvent = new PreCommandExecuteEvent(this, commandExecutor, cmd);
            eventManager.executeEvent(preEvent);

            if (preEvent.isCancelled()) return;

            if (commandExecutor.hasPermissions(cmd.getPermissions())) {
                CommandExecuteEvent event = new CommandExecuteEvent(this, commandExecutor, cmd);
                eventManager.executeEvent(event);

                if (event.isCancelled()) return;

                cmd.execute(commandExecutor, command, args);
                eventManager.executeEvent(new CommandExecutedEvent(this, commandExecutor, cmd));
            } else eventManager.executeEvent(new CommandExecutorMissingPermissionEvent(this, commandExecutor, cmd));
        } else eventManager.executeEvent(new CommandNotFoundEvent(this, commandExecutor, command, args));
    }
}
