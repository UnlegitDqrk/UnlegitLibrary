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

import java.lang.reflect.InvocationTargetException;

public class Client {
    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        new NetworkClient.ClientBuilder().setPacketHandler(new PacketHandler()).setEventManager(new EventManager()).
                setHost("localhost").setPort(25565).build().connect();
    }
}
