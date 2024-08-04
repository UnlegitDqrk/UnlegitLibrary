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
import me.finn.unlegitlibrary.event.impl.Event;

public class CommandNotFoundEvent extends Event {
    public final CommandManager commandManager;
    public final CommandExecutor commandExecutor;
    public final String name;
    public final String[] args;

    public CommandNotFoundEvent(CommandManager commandManager, CommandExecutor commandExecutor, String name, String[] args) {
        this.commandManager = commandManager;
        this.commandExecutor = commandExecutor;
        this.name = name;
        this.args = args;
    }
}
