/*
 * Copyright (C) 2025 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.network.system.server.events;

import me.finn.unlegitlibrary.event.impl.CancellableEvent;
import me.finn.unlegitlibrary.network.system.server.NetworkServer;

import java.net.Socket;

public class IncomingConnectionEvent extends CancellableEvent {
    public final NetworkServer server;
    public final Socket socket;

    public IncomingConnectionEvent(NetworkServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }
}
