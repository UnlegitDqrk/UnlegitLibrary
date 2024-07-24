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

package me.finn.unlegitlibrary.network.system.packets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class Packet {

    private final int id;

    public Packet(int id) {
        this.id = id;
    }

    public final int getPacketID() {
        return id;
    }

    public abstract void write(PacketHandler packetHandler, ObjectOutputStream outputStream) throws IOException, ClassNotFoundException;

    public abstract void read(PacketHandler packetHandler, ObjectInputStream outputStream) throws IOException, ClassNotFoundException;
}

