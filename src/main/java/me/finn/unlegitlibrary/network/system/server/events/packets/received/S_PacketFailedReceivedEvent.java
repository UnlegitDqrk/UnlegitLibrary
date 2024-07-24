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
import me.finn.unlegitlibrary.network.system.packets.Packet;

public class S_PacketFailedReceivedEvent extends Event {

    public final ConnectionHandler connectionHandler;
    public final Packet packet;
    public final Exception exception;

    public S_PacketFailedReceivedEvent(ConnectionHandler connectionHandler, Packet packet, Exception exception) {
        this.connectionHandler = connectionHandler;
        this.packet = packet;
        this.exception = exception;
    }
}
