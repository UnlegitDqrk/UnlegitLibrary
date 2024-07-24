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
import me.finn.unlegitlibrary.network.system.server.NetworkServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ClientDisconnectPacket extends SystemPacket {
    public ClientDisconnectPacket() {
        super(-2);
    }

    private int clientID;
    private boolean fromClient;

    public ClientDisconnectPacket(int clientID, boolean fromClient) {
        this();
        this.clientID = clientID;
        this.fromClient = fromClient;
    }

    @Override
    public void write(PacketHandler packetHandler, ObjectOutputStream outputStream) throws IOException, ClassNotFoundException {
        outputStream.writeBoolean(fromClient);
        outputStream.writeInt(clientID);
    }

    @Override
    public void read(PacketHandler packetHandler, ObjectInputStream outputStream) throws IOException, ClassNotFoundException {
        fromClient = outputStream.readBoolean();
        clientID = outputStream.readInt();

        if (fromClient) packetHandler.getServerInstance().getConnectionHandlerByID(clientID).disconnect(false);
        else packetHandler.getClientInstance().disconnect(false);
    }
}
