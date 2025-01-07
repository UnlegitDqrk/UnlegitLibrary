/*
 * Copyright (C) 2025 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.network.system.server.events;

import me.finn.unlegitlibrary.event.impl.Event;
import me.finn.unlegitlibrary.network.system.server.ConnectionHandler;

public final class ConnectionHandlerConnectedEvent extends Event {
    public final ConnectionHandler connectionHandler;

    public ConnectionHandlerConnectedEvent(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }
}
