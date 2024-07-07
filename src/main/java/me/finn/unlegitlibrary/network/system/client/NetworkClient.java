/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.network.system.client;

import me.finn.unlegitlibrary.event.EventManager;
import me.finn.unlegitlibrary.network.system.client.events.packets.received.C_PacketFailedReceivedEvent;
import me.finn.unlegitlibrary.network.system.client.events.packets.received.C_PacketReceivedEvent;
import me.finn.unlegitlibrary.network.system.client.events.packets.received.C_UnknownObjectReceivedEvent;
import me.finn.unlegitlibrary.network.system.client.events.packets.send.C_PacketFailedSendEvent;
import me.finn.unlegitlibrary.network.system.client.events.packets.send.C_PacketSendEvent;
import me.finn.unlegitlibrary.network.system.client.events.state.C_ConnectedEvent;
import me.finn.unlegitlibrary.network.system.client.events.state.C_DisconnectedEvent;
import me.finn.unlegitlibrary.network.system.client.events.state.C_StoppedEvent;
import me.finn.unlegitlibrary.network.system.packets.Packet;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;
import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class NetworkClient extends DefaultMethodsOverrider {

    private final String host;
    private final int port;
    private final PacketHandler packetHandler;
    private final EventManager eventManager;
    private final boolean autoReconnect;
    private final boolean debugLog;
    private final int maxAttempts;
    private final int attemptDelayInSec;

    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private int clientID = -1;
    private int attempt = 1;    private final Thread receiveThread = new Thread(this::receive);

    private boolean needClientID = false;
    private NetworkClient(String host, int port, PacketHandler packetHandler, EventManager eventManager, boolean autoReconnect, boolean debugLog, int maxAttempts, int attemptDelayInSec) {
        this.host = host;
        this.port = port;

        this.packetHandler = packetHandler;
        this.eventManager = eventManager;

        this.autoReconnect = autoReconnect;
        this.debugLog = debugLog;

        this.maxAttempts = maxAttempts;
        this.attemptDelayInSec = attemptDelayInSec;

        attempt = 1;
    }

    public final int getClientID() {
        return clientID;
    }

    public final Socket getSocket() {
        return socket;
    }

    public final ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public final ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public final boolean isDebugLogEnabled() {
        return debugLog;
    }

    public final boolean isAutoReconnectEnabled() {
        return autoReconnect;
    }

    public final PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public final boolean isNeedClientID() {
        return needClientID;
    }

    public final int getPort() {
        return port;
    }

    public final String getHost() {
        return host;
    }

    public final Thread getReceiveThread() {
        return receiveThread;
    }

    public final boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed() && socket.isBound()
                && receiveThread.isAlive() && !receiveThread.isInterrupted();
    }

    public synchronized final void connect() throws IOException, InterruptedException {
        if (isConnected()) return;
        if (debugLog) System.out.println("Connecting to server...");

        try {
            socket = new Socket(host, port);
            socket.setTcpNoDelay(false);

            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());

            needClientID = true;
            receiveThread.start();

            objectOutputStream.writeObject("c2s_connect");
            objectOutputStream.writeObject(clientID);
            objectOutputStream.flush();

            attempt = 1;
            if (debugLog) System.out.println("Connected to Server. Attempts: " + attempt);
        } catch (SocketException exception) {
            if (autoReconnect) reconnect();
            else throw exception;
        }
    }

    public final EventManager getEventManager() {
        return eventManager;
    }

    public synchronized final void disconnect() throws IOException {
        if (debugLog) System.out.println("Disconnecting from server...");

        if (isConnected()) {
            objectOutputStream.writeObject("c2s_disconnect");
            objectOutputStream.writeObject(clientID);
            objectOutputStream.flush();
        }

        eventManager.executeEvent(new C_DisconnectedEvent(this));
        if (debugLog) System.out.println("Disconnected from server.");
        stop();
    }

    private synchronized final void stop() throws IOException {
        if (debugLog) System.out.println("Stopping client...");

        if (isConnected()) {
            objectOutputStream.close();
            objectInputStream.close();
            socket.close();
        }

        objectOutputStream = null;
        objectInputStream = null;
        socket = null;

        needClientID = false;
        clientID = -1;
        attempt = 1;
        receiveThread.interrupt();

        eventManager.executeEvent(new C_StoppedEvent(this));
        if (debugLog) System.out.println("Client stopped.");
    }

    public final boolean sendPacket(Packet packet) throws IOException, ClassNotFoundException {
        if (!isConnected()) return false;

        if (packetHandler.sendPacket(packet, objectOutputStream)) {
            eventManager.executeEvent(new C_PacketSendEvent(this, packet));
            return true;
        } else {
            eventManager.executeEvent(new C_PacketFailedSendEvent(this, packet));
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
                    if (command.equalsIgnoreCase("s2c_connect")) {
                        clientID = id;
                        command = "";

                        eventManager.executeEvent(new C_ConnectedEvent(this));
                        continue;
                    } else if (command.equalsIgnoreCase("s2c_disconnect")) {
                        if (clientID != id) continue;
                        eventManager.executeEvent(new C_DisconnectedEvent(this));
                        command = "";

                        stop();
                        break;
                    } else if (packetHandler.getPacketByID(id) != null) {
                        command = "";
                        Packet packet = packetHandler.getPacketByID(id);

                        if (packetHandler.handlePacket(id, packet, objectInputStream))
                            eventManager.executeEvent(new C_PacketReceivedEvent(this, packet));
                        else eventManager.executeEvent(new C_PacketFailedReceivedEvent(this, packet));

                        continue;
                    }
                }

                eventManager.executeEvent(new C_UnknownObjectReceivedEvent(this, received));
            }
        } catch (EOFException exception) {
            attempt = 1;
            if (autoReconnect && maxAttempts == -1 || attempt <= maxAttempts) reconnect();
            else {
                eventManager.executeEvent(new C_StoppedEvent(this));
                exception.printStackTrace();
            }
        } catch (IOException | ClassNotFoundException exception) {
            eventManager.executeEvent(new C_StoppedEvent(this));
            exception.printStackTrace();
        }
    }

    private final void reconnect() {
        if (autoReconnect) {
            if (isConnected()) {
                try {
                    disconnect();
                } catch (IOException exception) {
                    if (maxAttempts > 0 && attempt > maxAttempts) {
                        exception.printStackTrace();
                        return;
                    }
                }
            }

            if (debugLog) System.out.println("Trying to reconnect... (Attempt: " + attempt++ + ")");

            try {
                Thread.sleep(attemptDelayInSec * 1000L);
                connect();
            } catch (InterruptedException | IOException exception) {
                if (maxAttempts == -1 || attempt <= maxAttempts) reconnect();
                else exception.printStackTrace();
            }
        } else {
            try {
                stop();
            } catch (IOException exception) {
                eventManager.executeEvent(new C_StoppedEvent(this));
                exception.printStackTrace();
            }
        }
    }

    public static class ClientBuilder {
        private int port;
        private String host;
        private PacketHandler packetHandler = new PacketHandler();
        private EventManager eventManager = new EventManager();
        private boolean autoReconnect = false;
        private boolean debugLog = false;
        private int maxAttempts = 10;
        private int attemptDelayInSec = 1;

        public final ClientBuilder enableAutoReconnect() {
            this.autoReconnect = true;
            return this;
        }

        public final ClientBuilder enableDebugLog() {
            this.debugLog = true;
            return this;
        }

        public final ClientBuilder setEventManager(EventManager eventManager) {
            this.eventManager = eventManager;
            return this;
        }

        public final ClientBuilder setPort(int port) {
            this.port = port;
            return this;
        }

        public final ClientBuilder setHost(String host) {
            this.host = host;
            return this;
        }

        public final ClientBuilder setPacketHandler(PacketHandler packetHandler) {
            this.packetHandler = packetHandler;
            return this;
        }

        public final ClientBuilder setAttemptDelayInSeconds(int attemptDelayInSec) {
            this.attemptDelayInSec = attemptDelayInSec;
            return this;
        }

        public final ClientBuilder setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
            return this;
        }

        public final NetworkClient build() {
            return new NetworkClient(host, port, packetHandler, eventManager, autoReconnect, debugLog, maxAttempts, attemptDelayInSec);
        }
    }




}
