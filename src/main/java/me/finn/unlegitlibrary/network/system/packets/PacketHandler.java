/*
 * Copyright (C) 2025 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.network.system.packets;

import me.finn.unlegitlibrary.network.system.client.NetworkClient;
import me.finn.unlegitlibrary.network.system.server.NetworkServer;
import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public final class PacketHandler extends DefaultMethodsOverrider {

    private final Map<Integer, Packet> packets = new HashMap<>();

    private NetworkClient clientInstance;
    private NetworkServer serverInstance;

    public NetworkClient getClientInstance() {
        return clientInstance;
    }

    public void setClientInstance(NetworkClient clientInstance) {
        if (this.clientInstance == null) this.clientInstance = clientInstance;
    }

    public NetworkServer getServerInstance() {
        return serverInstance;
    }

    public void setServerInstance(NetworkServer serverInstance) {
        if (this.serverInstance == null) this.serverInstance = serverInstance;
    }

    public boolean isPacketIDRegistered(int id) {
        return packets.containsKey(id);
    }

    public Packet getPacketByID(int id) {
        return packets.get(id);
    }

    public boolean registerPacket(Packet packet) {
        int id = packet.getPacketID();

        if (isPacketIDRegistered(id)) return false;

        packets.put(id, packet);
        return true;
    }

    public boolean handlePacket(int id, Packet packet, ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        if (!isPacketIDRegistered(id) || (packet != null && id != packet.getPacketID()) || (packet != null && !isPacketIDRegistered(packet.getPacketID())))
            return false;

        packet.read(this, inputStream);
        return true;
    }

    public boolean sendPacket(Packet packet, ObjectOutputStream outputStream) throws IOException, ClassNotFoundException {
        int id = packet.getPacketID();
        if (!isPacketIDRegistered(id)) return false;

        outputStream.writeObject(id);
        packet.write(this, outputStream);
        outputStream.flush();

        return true;
    }
}