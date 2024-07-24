/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.network.system.server.events.packets.send;

import me.finn.unlegitlibrary.event.impl.Event;
import me.finn.unlegitlibrary.network.system.server.ConnectionHandler;
import me.finn.unlegitlibrary.network.system.packets.Packet;

public class S_PacketSendEvent extends Event {

    public final ConnectionHandler connectionHandler;
    public final Packet packet;

    public S_PacketSendEvent(ConnectionHandler connectionHandler, Packet packet) {
        this.connectionHandler = connectionHandler;
        this.packet = packet;
    }
}
