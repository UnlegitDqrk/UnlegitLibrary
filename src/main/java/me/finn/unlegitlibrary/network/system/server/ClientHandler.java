/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.network.system.server;

import me.finn.unlegitlibrary.network.system.packets.Packet;
import me.finn.unlegitlibrary.network.system.server.events.client.packets.received.S_PacketFailedReceivedEvent;
import me.finn.unlegitlibrary.network.system.server.events.client.packets.received.S_PacketReceivedEvent;
import me.finn.unlegitlibrary.network.system.server.events.client.packets.received.S_UnknownObjectReceivedEvent;
import me.finn.unlegitlibrary.network.system.server.events.client.packets.send.S_PacketFailedSendEvent;
import me.finn.unlegitlibrary.network.system.server.events.client.packets.send.S_PacketSendEvent;
import me.finn.unlegitlibrary.network.system.server.events.client.state.S_ClientConnectedEvent;
import me.finn.unlegitlibrary.network.system.server.events.client.state.S_ClientDisconnectedEvent;
import me.finn.unlegitlibrary.network.system.server.events.client.state.S_ClientStoppedEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler {

    private final NetworkServer networkServer;
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private final Thread receiveThread = new Thread(this::receive);

    private int clientID;

    public ClientHandler(NetworkServer networkServer, Socket socket, int clientID) throws IOException {
        this.networkServer = networkServer;
        this.socket = socket;
        this.clientID = clientID;

        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());

        receiveThread.start();
    }

    public final int getClientID() {
        return clientID;
    }

    public final Socket getSocket() {
        return socket;
    }

    public final NetworkServer getNetworkServer() {
        return networkServer;
    }

    public final ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public final ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public final Thread getReceiveThread() {
        return receiveThread;
    }

    public final boolean isConnected() {
        return networkServer.isRunning() && socket != null && socket.isConnected() && !socket.isClosed() && socket.isBound()
                && receiveThread.isAlive() && !receiveThread.isInterrupted();
    }

    public synchronized final void disconnect() throws IOException {
        if (isConnected()) {
            objectOutputStream.writeUTF("s2c_disconnect");
            objectOutputStream.writeInt(clientID);
            objectOutputStream.flush();
        }

        networkServer.getEventManager().executeEvent(new S_ClientDisconnectedEvent(this));
        stop();
    }

    private synchronized final void stop() throws IOException {
        if (isConnected()) {
            objectOutputStream.close();
            objectInputStream.close();
            socket.close();
        }

        objectOutputStream = null;
        objectInputStream = null;
        socket = null;

        clientID = -1;
        receiveThread.interrupt();

        networkServer.getClientHandlers().remove(this);
        networkServer.getEventManager().executeEvent(new S_ClientStoppedEvent(this));
    }

    public final boolean sendPacket(Packet packet) throws IOException, ClassNotFoundException {
        if (!isConnected()) return false;

        if (networkServer.getPacketHandler().sendPacket(packet, objectOutputStream)) {
            networkServer.getEventManager().executeEvent(new S_PacketSendEvent(this, packet));
            return true;
        } else {
            networkServer.getEventManager().executeEvent(new S_PacketFailedSendEvent(this, packet));
            return false;
        }
    }

    private final void receive() {
        if (!isConnected()) return;

        try {
            String command = "";

            while (isConnected()) {
                Object received = objectInputStream.readObject();

                if (received instanceof String) {
                    command = (String) received;
                    continue;
                }

                if (received instanceof Integer id) {
                    if (command.equalsIgnoreCase("c2s_connect")) {
                        if (clientID == id) continue;
                        command = "";

                        objectOutputStream.writeObject("s2c_connect");
                        objectOutputStream.writeObject(clientID);
                        objectOutputStream.flush();

                        networkServer.getEventManager().executeEvent(new S_ClientConnectedEvent(this));
                            continue;
                    } else if (command.equalsIgnoreCase("c2s_disconnect")) {
                        if (clientID != id) continue;
                        networkServer.getEventManager().executeEvent(new S_ClientDisconnectedEvent(this));
                        command = "";

                        stop();
                        break;
                    } else if (networkServer.getPacketHandler().getPacketByID(id) != null) {
                        command = "";
                        Packet packet = networkServer.getPacketHandler().getPacketByID(id);

                        if (networkServer.getPacketHandler().handlePacket(id, packet, objectInputStream))
                            networkServer.getEventManager().executeEvent(new S_PacketReceivedEvent(this, packet));
                        else networkServer.getEventManager().executeEvent(new S_PacketFailedReceivedEvent(this, packet));

                        continue;
                    }
                }

                networkServer.getEventManager().executeEvent(new S_UnknownObjectReceivedEvent(this, received));
            }
        } catch (SocketException exception) {
            networkServer.getEventManager().executeEvent(new S_ClientDisconnectedEvent(this));
            try {
                stop();
            } catch (IOException ioException) {
                networkServer.getClientHandlers().remove(this);
                networkServer.getEventManager().executeEvent(new S_ClientStoppedEvent(this));

                exception.printStackTrace();
                ioException.printStackTrace();
            }
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }
}
