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

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.net.Socket;
import java.security.cert.X509Certificate;

public class IncomingConnectionEvent extends CancellableEvent {
    public final NetworkServer server;
    public final SSLSocket socket;

    public IncomingConnectionEvent(NetworkServer server, SSLSocket socket) {
        this.server = server;
        this.socket = socket;
    }
}
