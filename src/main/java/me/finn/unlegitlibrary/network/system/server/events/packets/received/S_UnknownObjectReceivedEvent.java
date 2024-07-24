/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.network.system.server.events.packets.received;

import me.finn.unlegitlibrary.event.impl.Event;
import me.finn.unlegitlibrary.network.system.server.ConnectionHandler;

public class S_UnknownObjectReceivedEvent extends Event {
    public final ConnectionHandler connectionHandler;
    public final Object received;

    public S_UnknownObjectReceivedEvent(ConnectionHandler connectionHandler, Object received) {
        this.connectionHandler = connectionHandler;
        this.received = received;
    }
}
