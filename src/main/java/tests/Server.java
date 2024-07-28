/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package tests;

import me.finn.unlegitlibrary.event.EventManager;
import me.finn.unlegitlibrary.network.system.client.NetworkClient;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;
import me.finn.unlegitlibrary.network.system.server.NetworkServer;

import java.lang.reflect.InvocationTargetException;

public class Server {
    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        new NetworkServer.ServerBuilder().setPacketHandler(new PacketHandler()).setEventManager(new EventManager()).
                setPort(25565).build().start();
    }
}
