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
import java.util.HashSet;
import java.util.List;

public abstract class CommandExecutor {

    public final String name;
    private final List<CommandPermission> permissions;

    public List<CommandPermission> getPermissions() {
        return new ArrayList<>(permissions);
    }

    public boolean hasPermission(CommandPermission permission) {
        return permissions.contains(permission);
    }

    public boolean hasPermissions(List<CommandPermission> permissions) {
        return new HashSet<>(this.permissions).containsAll(permissions);
    }

    public CommandExecutor(String name, CommandPermission... permissions) {
        this.name = name;

        this.permissions = new ArrayList<>();
        this.permissions.addAll(Arrays.asList(permissions));
    }


}
