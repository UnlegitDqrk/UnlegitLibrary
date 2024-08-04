/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.command.events;

import me.finn.unlegitlibrary.command.Command;
import me.finn.unlegitlibrary.command.CommandExecutor;
import me.finn.unlegitlibrary.command.CommandManager;
import me.finn.unlegitlibrary.event.impl.CancellableEvent;

public class PreCommandExecuteEvent extends CancellableEvent {
    public final CommandManager commandManager;
    public final CommandExecutor commandExecutor;
    public final Command command;

    public PreCommandExecuteEvent(CommandManager commandManager, CommandExecutor commandExecutor, Command command) {
        this.commandManager = commandManager;
        this.commandExecutor = commandExecutor;
        this.command = command;
    }
}
