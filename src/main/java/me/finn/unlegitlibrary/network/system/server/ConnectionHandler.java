/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.network.system.server;

import me.finn.unlegitlibrary.network.system.packets.impl.ClientIDPacket;
import me.finn.unlegitlibrary.network.system.packets.impl.ClientDisconnectPacket;
import me.finn.unlegitlibrary.network.system.server.events.packets.received.S_PacketFailedReceivedEvent;
import me.finn.unlegitlibrary.network.system.server.events.packets.received.S_PacketReceivedEvent;
import me.finn.unlegitlibrary.network.system.server.events.packets.received.S_ReceiveThreadFailedEvent;
import me.finn.unlegitlibrary.network.system.server.events.packets.received.S_UnknownObjectReceivedEvent;
import me.finn.unlegitlibrary.network.system.server.events.packets.send.S_PacketFailedSendEvent;
import me.finn.unlegitlibrary.network.system.server.events.packets.send.S_PacketSendEvent;
import me.finn.unlegitlibrary.network.system.server.events.state.connection.S_ConnectionHandlerConnectedEvent;
import me.finn.unlegitlibrary.network.system.server.events.state.connection.S_ConnectionHandlerDisconnectedEvent;
import me.finn.unlegitlibrary.network.system.packets.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionHandler {

    private NetworkServer server;
    private Socket socket;
    private int clientID;

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    public final Thread receiveThread = new Thread(this::receive);

    public ConnectionHandler(NetworkServer server, Socket socket, int clientID) throws IOException {
        this.server = server;
        this.socket = socket;
        this.clientID = clientID;

        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());

        receiveThread.start();

        sendPacket(new ClientIDPacket(clientID));
        server.getEventManager().executeEvent(new S_ConnectionHandlerConnectedEvent(this));
    }

    public final int getClientID() {
        return clientID;
    }

    public final NetworkServer getServer() {
        return server;
    }

    public final Socket getSocket() {
        return socket;
    }

    public final Thread getReceiveThread() {
        return receiveThread;
    }

    public final boolean isConnected() {
        return server.isRunning() && socket != null && socket.isConnected() && !socket.isClosed() && socket.isBound()
                && receiveThread.isAlive() && !receiveThread.isInterrupted();
    }

    public final boolean sendPacket(Packet packet) {
        if (!isConnected()) return false;

        try {
            if (server.getPacketHandler().sendPacket(packet, outputStream)) {
                server.getEventManager().executeEvent(new S_PacketSendEvent(this, packet));
                return true;
            } else server.getEventManager().executeEvent(new S_PacketFailedSendEvent(this, packet, null));
        } catch (IOException | ClassNotFoundException exception) {
            if (server.getLogger() == null) System.err.println("Failed to send packet: " + exception.getMessage());
            else server.getLogger().exception("Failed to connect to send packet", exception);

            server.getEventManager().executeEvent(new S_PacketFailedSendEvent(this, packet, exception));
        }

        return false;
    }

    public synchronized boolean disconnect(boolean sendDisconnectPacket) {
        if (server.getLogger() == null) System.out.println("Disconnecting from server...");
        else server.getLogger().info("Disconnecting from server...");

        receiveThread.interrupt();

        if (isConnected()) {
            if (sendDisconnectPacket) sendPacket(new ClientDisconnectPacket(clientID, false));

            try {
                outputStream.close();
                inputStream.close();
                socket.close();
            } catch (IOException exception) {
                if (server.getLogger() == null) System.err.println("Failed to close socket: " + exception.getMessage());
                else server.getLogger().exception("Failed to close socket", exception);
            }
        }

        outputStream = null;
        inputStream = null;
        socket = null;

        server.getConnectionHandlers().remove(this);
        clientID = -1;

        server.getEventManager().executeEvent(new S_ConnectionHandlerDisconnectedEvent(this));
        if (server.getLogger() == null) System.out.println("Disconnected from server");
        else server.getLogger().info("Disconnected from server...");

        return true;
    }

    private void receive() {
        if (!isConnected()) return;

        while (isConnected()) {
            try {
                Object received = inputStream.readObject();

                if (received instanceof Integer) {
                    int id = (Integer) received;
                    Packet packet = server.getPacketHandler().getPacketByID(id);
                    if (server.getPacketHandler().handlePacket(id, packet, inputStream))
                        server.getEventManager().executeEvent(new S_PacketReceivedEvent(this, packet));
                    else server.getEventManager().executeEvent(new S_PacketFailedReceivedEvent(this, packet, null));
                } else server.getEventManager().executeEvent(new S_UnknownObjectReceivedEvent(this, received));
            } catch (IOException | ClassNotFoundException exception) {
                if (server.getLogger() == null) System.err.println("Receive thread failed: " + exception.getMessage());
                else server.getLogger().exception("Receive thread failed", exception);

                server.getEventManager().executeEvent(new S_ReceiveThreadFailedEvent(this, exception));
            }
        }

        disconnect(false);
    }
}
