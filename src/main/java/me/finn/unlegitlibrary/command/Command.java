/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Command {

    public final CommandManager commandManager;
    public final String name;
    public final String description;
    public final String usage;
    private final List<CommandPermission> permissions;
    private final List<String> aliases;

    public final List<CommandPermission> getPermissions() {
        return new ArrayList<>(permissions);
    }

    public final List<String> getAliases() {
        return new ArrayList<>(aliases);
    }

    public Command(CommandManager commandManager, String name, String description, String usage, List<CommandPermission> permissions, List<String> aliases) {
        this.commandManager = commandManager;
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.permissions = permissions;
        this.aliases = aliases;
    }

    public abstract void execute(CommandExecutor commandExecutor, String label, String[] args);
}
