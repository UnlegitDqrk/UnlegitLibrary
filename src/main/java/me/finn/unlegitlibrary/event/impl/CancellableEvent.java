/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.event.impl;

public class CancellableEvent extends Event {

    private boolean isCancelled;

    public final boolean isCancelled() {
        return isCancelled;
    }

    public final void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

}