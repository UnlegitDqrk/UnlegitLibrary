/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.network.system.packets;

import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class PacketHandler extends DefaultMethodsOverrider {

    private final Map<Integer, Class<? extends Packet>> packets = new HashMap<>();

    public final boolean isPacketIDRegistered(int id) {
        return packets.containsKey(id);
    }

    public final Packet getPacketByID(int id) {
        Class<? extends Packet> packetClass = packets.get(id);
        if (packetClass == null) return null;
        try {
            return packetClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException |
                 NoSuchMethodException exception) {
            return null;
        }
    }

    public final boolean registerPacket(Class<? extends Packet> packetClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Packet packet = packetClass.getDeclaredConstructor().newInstance();
        int id = packet.getPacketID();

        if (isPacketIDRegistered(id)) return false;
        packets.put(id, packetClass);
        return true;
    }

    public final boolean handlePacket(int id, Packet packet, ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        if (!isPacketIDRegistered(id) || (packet != null && id != packet.getPacketID()) || (packet != null && !isPacketIDRegistered(packet.getPacketID())))
            return false;

        packet.read(inputStream);
        return true;
    }

    public final boolean sendPacket(Packet packet, ObjectOutputStream outputStream) throws IOException, ClassNotFoundException {
        int id = packet.getPacketID();
        if (!isPacketIDRegistered(id)) return false;

        outputStream.writeObject(id);
        packet.write(outputStream);
        outputStream.flush();

        return true;
    }
}