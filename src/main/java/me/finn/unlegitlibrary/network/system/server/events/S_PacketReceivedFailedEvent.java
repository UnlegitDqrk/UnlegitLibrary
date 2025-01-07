/*
 * Copyright (C) 2025 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.network.system.server.events;

import me.finn.unlegitlibrary.event.impl.Event;
import me.finn.unlegitlibrary.network.system.packets.Packet;
import me.finn.unlegitlibrary.network.system.server.ConnectionHandler;

public class S_PacketReceivedFailedEvent extends Event {
    public final ConnectionHandler connectionHandler;
    public final Packet packet;

    public S_PacketReceivedFailedEvent(ConnectionHandler connectionHandler, Packet packet) {
        this.connectionHandler = connectionHandler;
        this.packet = packet;
    }
}
