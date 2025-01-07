/*
 * Copyright (C) 2025 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.network.system.server;

import me.finn.unlegitlibrary.network.system.packets.Packet;
import me.finn.unlegitlibrary.network.system.packets.impl.ClientIDPacket;
import me.finn.unlegitlibrary.network.system.server.events.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ConnectionHandler {

    public final NetworkServer networkServer;
    private Socket socket;
    private int clientID;

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    public final Thread receiveThread = new Thread(this::receive);

    public int getClientID() {
        return clientID;
    }

    public final boolean isConnected() {
        return networkServer.isRunning() && socket != null && socket.isConnected() && !socket.isClosed() && socket.isBound()
                && receiveThread.isAlive() && !receiveThread.isInterrupted();
    }

    public ConnectionHandler(NetworkServer server, Socket socket, int clientID) throws IOException, ClassNotFoundException {
        this.networkServer = server;
        this.socket = socket;
        this.clientID = clientID;

        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());

        receiveThread.start();

        sendPacket(new ClientIDPacket());
        networkServer.getEventManager().executeEvent(new ConnectionHandlerConnectedEvent(this));
    }

    public synchronized boolean disconnect() {
        boolean wasConnected = isConnected();

        if (wasConnected) {
            if (networkServer.getLogger() == null)
                System.out.println("Client ID '" + clientID + "' is disconnecting from server...");
            else networkServer.getLogger().info("Client ID '" + clientID + "' is disconnecting from server...");
        }

        if (receiveThread.isAlive() && !receiveThread.isInterrupted()) receiveThread.interrupt();

        if (wasConnected) {
            try {
                outputStream.close();
                inputStream.close();
                socket.close();
            } catch (IOException exception) {
                if (networkServer.getLogger() == null) System.err.println("Client ID '" + clientID + "' failed to close socket: " + exception.getMessage());
                else networkServer.getLogger().exception("Client ID '" + clientID + "' failed to close socket", exception);
            }
        }

        outputStream = null;
        inputStream = null;
        socket = null;

        networkServer.getConnectionHandlers().remove(this);

        if (wasConnected) {
            if (networkServer.getLogger() == null)
                System.out.println("Client ID '" + clientID + "' disconnected from server");
            else networkServer.getLogger().info("Client ID '" + clientID + "' disconnected from server");
        }

        networkServer.getEventManager().executeEvent(new ConnectionHandlerDisconnectedEvent(this));
        clientID = -1;
        return true;
    }

    public boolean sendPacket(Packet packet) throws IOException, ClassNotFoundException {
        if (!isConnected()) return false;

        if (networkServer.getPacketHandler().sendPacket(packet, outputStream)) {
            networkServer.getEventManager().executeEvent(new S_PacketSendEvent(packet, this));
            return true;
        } else {
            networkServer.getEventManager().executeEvent(new S_PacketSendFailedEvent(packet, this));
            return false;
        }
    }

    private void receive() {
        if (!isConnected()) return;

        while (isConnected()) {
            try {
                Object received = inputStream.readObject();

                if (received instanceof Integer) {
                    int packetID = (Integer) received;
                    if (networkServer.getPacketHandler().isPacketIDRegistered(packetID)) {
                        Packet packet = networkServer.getPacketHandler().getPacketByID(packetID);
                        if (networkServer.getPacketHandler().handlePacket(packetID, packet, inputStream))
                            networkServer.getEventManager().executeEvent(new S_PacketReceivedEvent(this, packet));
                        else
                            networkServer.getEventManager().executeEvent(new S_PacketReceivedFailedEvent(this, packet));
                    } else networkServer.getEventManager().executeEvent(new S_UnknownObjectReceivedEvent(received, this));
                } else networkServer.getEventManager().executeEvent(new S_UnknownObjectReceivedEvent(received, this));
            } catch (SocketException ignored) {
                disconnect();
            } catch (Exception exception) {
                exception.printStackTrace();
                return;
            }
        }
    }
}
