/*
 * Copyright (C) 2025 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.network.system.client.events;

import me.finn.unlegitlibrary.event.impl.Event;
import me.finn.unlegitlibrary.network.system.client.NetworkClient;
import me.finn.unlegitlibrary.network.system.packets.Packet;

public class C_PacketSendFailedEvent extends Event {
    public final NetworkClient networkClient;
    public final Packet packet;

    public C_PacketSendFailedEvent(NetworkClient networkClient, Packet packet) {
        this.networkClient = networkClient;
        this.packet = packet;
    }
}
