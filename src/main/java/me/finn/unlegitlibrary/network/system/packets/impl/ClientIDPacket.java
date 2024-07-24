/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.network.system.packets.impl;

import me.finn.unlegitlibrary.network.system.client.NetworkClient;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;
import me.finn.unlegitlibrary.network.system.packets.SystemPacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ClientIDPacket extends SystemPacket {

    public ClientIDPacket() {
        super(-1);
    }

    private int clientID;

    public ClientIDPacket(int clientID) {
        this();
        this.clientID = clientID;
    }

    @Override
    public void write(PacketHandler packetHandler, ObjectOutputStream outputStream) throws IOException, ClassNotFoundException {
        outputStream.writeInt(clientID);
    }

    @Override
    public void read(PacketHandler packetHandler, ObjectInputStream outputStream) throws IOException, ClassNotFoundException {
        clientID = outputStream.readInt();
        packetHandler.getClientInstance().setClientID(clientID);
    }
}
