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

public class S_UnknownObjectReceivedEvent extends Event {

    public final Object received;
    public final ConnectionHandler connectionHandler;

    public S_UnknownObjectReceivedEvent(Object received, ConnectionHandler connectionHandler) {
        this.received = received;
        this.connectionHandler = connectionHandler;
    }
}
