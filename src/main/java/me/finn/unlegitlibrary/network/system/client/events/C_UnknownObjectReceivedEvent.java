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

public class C_UnknownObjectReceivedEvent extends Event {
    public final NetworkClient networkClient;
    public final Object received;

    public C_UnknownObjectReceivedEvent(NetworkClient networkClient, Object received) {
        this.networkClient = networkClient;
        this.received = received;
    }
}
