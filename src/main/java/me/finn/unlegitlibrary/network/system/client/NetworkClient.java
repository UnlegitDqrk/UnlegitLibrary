/*
 * Copyright (C) 2025 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.network.system.client;

import me.finn.unlegitlibrary.event.EventManager;
import me.finn.unlegitlibrary.network.system.client.events.send.C_PacketFailedSendEvent;
import me.finn.unlegitlibrary.network.system.client.events.state.C_DisconnectedEvent;
import me.finn.unlegitlibrary.network.system.packets.impl.ClientDisconnectPacket;
import me.finn.unlegitlibrary.network.system.client.events.*;
import me.finn.unlegitlibrary.network.system.packets.Packet;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;
import me.finn.unlegitlibrary.network.system.packets.impl.ClientIDPacket;
import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;
import me.finn.unlegitlibrary.utils.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;

public final class NetworkClient {
    public static class ClientBuilder extends DefaultMethodsOverrider {
        private String host;
        private int port;

        private PacketHandler packetHandler;
        private EventManager eventManager;
        private Logger logger;

        private int maxReconnectAttempts = 0;
        private int reconnectDelay = 3000;
        private int timeout = 0;

        public final NetworkClient build() {
            return new NetworkClient(host, port ,packetHandler, eventManager, logger, maxReconnectAttempts, reconnectDelay, timeout);
        }

        public final ClientBuilder setEventManager(EventManager eventManager) {
            this.eventManager = eventManager;
            return this;
        }

        public final ClientBuilder setHost(String host) {
            this.host = host;
            return this;
        }

        public final ClientBuilder setLogger(Logger logger) {
            this.logger = logger;
            return this;
        }

        public final ClientBuilder setMaxReconnectAttempts(int maxReconnectAttempts) {
            this.maxReconnectAttempts = maxReconnectAttempts;
            return this;
        }

        public final ClientBuilder setPacketHandler(PacketHandler packetHandler) {
            this.packetHandler = packetHandler;
            return this;
        }

        public final ClientBuilder setPort(int port) {
            this.port = port;
            return this;
        }

        public final ClientBuilder setReconnectDelay(int reconnectDelay) {
            this.reconnectDelay = reconnectDelay;
            return this;
        }

        public final ClientBuilder setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }
    }

    private final String host;
    private final int port;

    private final PacketHandler packetHandler;
    private final EventManager eventManager;
    private final Logger logger;

    private Socket socket;
    private int timeout;

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private int clientID;

    private int currentAttempts;
    private final int maxReconnectAttempts;
    private final int reconnectDelay;

    private final Thread receiveThread = new Thread(this::receive);

    public int getClientID() {
        return clientID;
    }

    public Socket getSocket() {
        return socket;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public Logger getLogger() {
        return logger;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed() && socket.isBound()
                && receiveThread.isAlive() && !receiveThread.isInterrupted();
    }

    public boolean isAutoReconnectEnabled() {
        return maxReconnectAttempts != 0;
    }

    private NetworkClient(String host, int port, PacketHandler packetHandler, EventManager eventManager, Logger logger, int reconnectAttempts, int reconnectDelay, int timeout) {
        this.host = host;
        this.port = port;
        this.clientID = -1;
        this.timeout = timeout;

        this.packetHandler = packetHandler;
        this.eventManager = eventManager;
        this.logger = logger;

        this.maxReconnectAttempts = reconnectAttempts;
        this.reconnectDelay = reconnectDelay;
        this.currentAttempts = 0;

        this.packetHandler.setClientInstance(this);
        this.packetHandler.registerPacket(new ClientIDPacket());
    }

    public synchronized boolean connect() throws ConnectException {
        if (isConnected()) return false;

        if (logger == null) System.out.println("Trying to connect to " + host + ":" + port + "...");
        else logger.info("Trying to connect to " + host + ":" + port + "...");

        try {
            socket = new Socket(host, port);
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(timeout);

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

            receiveThread.start();

            if (currentAttempts == 0) currentAttempts++;
            if (logger == null) System.out.println("Connected to " + host + ":" + port + " (Attempts: " + currentAttempts + ")");
            else logger.info("Connected to " + host + ":" + port + " (Attempts: " + currentAttempts + ")");

            eventManager.executeEvent(new ClientConnectedEvent(this));

            currentAttempts = 0;
            return true;
        } catch (IOException exception) {
            if (isAutoReconnectEnabled()) {
                try {
                    Thread.sleep(reconnectDelay);
                } catch (InterruptedException sleepThreadException) {
                    if (logger == null) System.err.println("Reconnect exception: " + sleepThreadException.getMessage());
                    else logger.exception("Reconnect exception", sleepThreadException);
                }

                currentAttempts++;
                if (currentAttempts < maxReconnectAttempts || maxReconnectAttempts < 0) return connect();
            }
        }

        throw new ConnectException("Failed to connect to " + host + ":" + port);
    }

    private void receive() {
        if (!isConnected()) return;

        while (isConnected()) {
            try {
                Object received = inputStream.readObject();

                if (received instanceof Integer) {
                    int packetID = (Integer) received;
                    if (packetHandler.isPacketIDRegistered(packetID)) {
                        Packet packet = packetHandler.getPacketByID(packetID);
                        if (packetHandler.handlePacket(packetID, packet, inputStream))
                            eventManager.executeEvent(new C_PacketReceivedEvent(this, packet));
                        else
                            eventManager.executeEvent(new C_PacketReceivedFailedEvent(this, packet));
                    } else eventManager.executeEvent(new C_UnknownObjectReceivedEvent(this, received));
                } else eventManager.executeEvent(new C_UnknownObjectReceivedEvent(this, received));
            } catch (SocketException ignored) {
                try {
                    disconnect();
                } catch (ConnectException exception) {
                    exception.printStackTrace();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                return;
            }
        }
    }


    public void setClientID(int clientID) {
        if (this.clientID == -1) this.clientID = clientID;
    }

    public boolean sendPacket(Packet packet) throws IOException, ClassNotFoundException {
        if (!isConnected()) return false;

        try {
            if (packetHandler.sendPacket(packet, outputStream)) {
                eventManager.executeEvent(new C_PacketSendEvent(this, packet));
                return true;
            } else {
                eventManager.executeEvent(new C_PacketSendFailedEvent(this, packet));
                return false;
            }
        } catch (IOException | ClassNotFoundException exception) {
            throw exception;
        }
    }

    public synchronized boolean disconnect() throws ConnectException {
        boolean wasConnected = isConnected();

        if (wasConnected) {
            if (logger == null) System.out.println("Disconnecting from server...");
            else logger.info("Disconnecting from server...");
        }

        if (receiveThread.isAlive() && !receiveThread.isInterrupted()) receiveThread.interrupt();

        if (wasConnected) {
            try {
                outputStream.close();
                inputStream.close();
                socket.close();
            } catch (IOException exception) {
                if (logger == null) System.err.println("Failed to close socket: " + exception.getMessage());
                else logger.exception("Failed to close socket", exception);
            }
        }

        outputStream = null;
        inputStream = null;
        socket = null;

        currentAttempts = 0;

        if (wasConnected) {
            if (logger == null) System.out.println("Disconnected from server");
            else logger.info("Disconnected from server");
        }

        eventManager.executeEvent(new ClientDisconnectedEvent(this));

        clientID = -1;
        if (isAutoReconnectEnabled() && (currentAttempts < maxReconnectAttempts || maxReconnectAttempts < 0))
            return connect();

        return true;
    }
}
