/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.network.system.client;

import me.finn.unlegitlibrary.event.EventManager;
import me.finn.unlegitlibrary.network.system.client.events.received.C_PacketFailedReceivedEvent;
import me.finn.unlegitlibrary.network.system.client.events.received.C_PacketReceivedEvent;
import me.finn.unlegitlibrary.network.system.client.events.received.C_UnknownObjectReceivedEvent;
import me.finn.unlegitlibrary.network.system.client.events.send.C_PacketFailedSendEvent;
import me.finn.unlegitlibrary.network.system.client.events.send.C_PacketSendEvent;
import me.finn.unlegitlibrary.network.system.client.events.state.C_ConnectedEvent;
import me.finn.unlegitlibrary.network.system.client.events.state.C_DisconnectedEvent;
import me.finn.unlegitlibrary.network.system.client.events.state.C_ReceiveThreadFailedEvent;
import me.finn.unlegitlibrary.network.system.packets.Packet;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;
import me.finn.unlegitlibrary.network.system.packets.impl.ClientDisconnectPacket;
import me.finn.unlegitlibrary.network.system.packets.impl.ClientIDPacket;
import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;
import me.finn.unlegitlibrary.utils.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.SocketException;

public class NetworkClient extends DefaultMethodsOverrider {

    public static class ClientBuilder extends DefaultMethodsOverrider {
        private String host;
        private int port;

        private PacketHandler packetHandler;
        private EventManager eventManager;
        private Logger logger;

        private int maxReconnectAttempts = 0;
        private int reconnectDelay = 3000;
        private int timeout = 0;

        public final NetworkClient build() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
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

    private int currentAttempts;
    private final int maxReconnectAttempts;
    private final int reconnectDelay;

    private Socket socket;
    private int timeout;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private int clientID = -1;
    public final Thread receiveThread = new Thread(this::receive);

    private NetworkClient(String host, int port, PacketHandler packetHandler, EventManager eventManager, Logger logger, int reconnectAttempts, int reconnectDelay, int timeout) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
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
        this.packetHandler.registerPacket(ClientDisconnectPacket.class);
        this.packetHandler.registerPacket(ClientIDPacket.class);
    }

    public final int getClientID() {
        return clientID;
    }

    public final Socket getSocket() {
        return socket;
    }

    public final EventManager getEventManager() {
        return eventManager;
    }

    public final String getHost() {
        return host;
    }

    public final int getPort() {
        return port;
    }

    public final ObjectInputStream getInputStream() {
        return inputStream;
    }

    public final ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public final PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public final boolean isAutoReconnectEnabled() {
        return maxReconnectAttempts != 0;
    }

    public final Logger getLogger() {
        return logger;
    }

    public final boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed() && socket.isBound()
                && receiveThread.isAlive() && !receiveThread.isInterrupted();
    }

    public final Thread getReceiveThread() {
        return receiveThread;
    }

    public final void setClientID(int clientID) {
        if (this.clientID == -1) {
            this.clientID = clientID;
            eventManager.executeEvent(new C_ConnectedEvent(this));
        }
    }

    public synchronized boolean disconnect(boolean sendDisconnectPacket) {
        if (logger == null) System.out.println("Disconnecting from server...");
        else logger.info("Disconnecting from server...");

        receiveThread.interrupt();

        if (isConnected()) {
            if (sendDisconnectPacket) sendPacket(new ClientDisconnectPacket(clientID, true));

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

        clientID = -1;
        currentAttempts = 0;

        eventManager.executeEvent(new C_DisconnectedEvent(this));
        if (logger == null) System.out.println("Disconnected from server");
        else logger.info("Disconnected from serverß");

        if (maxReconnectAttempts != 0) {
            try {
                Thread.sleep(reconnectDelay);
            } catch (InterruptedException sleepThreadException) {
                if (logger == null) System.err.println("Reconnect exception: " + sleepThreadException.getMessage());
                else logger.exception("Reconnect exception", sleepThreadException);
            }

            currentAttempts++;
            if (currentAttempts <= maxReconnectAttempts || maxReconnectAttempts < 0) return connect();
        }

        return true;
    }

    public synchronized final boolean connect() {
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

            currentAttempts = 0;
            return true;
        } catch (IOException exception) {
            if (maxReconnectAttempts != 0) {
                try {
                    Thread.sleep(reconnectDelay);
                } catch (InterruptedException sleepThreadException) {
                    if (logger == null) System.err.println("Reconnect exception: " + sleepThreadException.getMessage());
                    else logger.exception("Reconnect exception", sleepThreadException);
                }

                currentAttempts++;
                if (currentAttempts <= maxReconnectAttempts || maxReconnectAttempts < 0) return connect();
            }

            if (logger == null) System.err.println("Failed to connect to " + host + ":" + port + ": " + exception.getMessage());
            else logger.exception("Failed to connect to " + host + ":" + port, exception);
        }

        return false;
    }

    public final boolean sendPacket(Packet packet) {
        if (!isConnected()) return false;

        try {
            if (packetHandler.sendPacket(packet, outputStream)) {
                eventManager.executeEvent(new C_PacketSendEvent(this, packet));
                return true;
            } else eventManager.executeEvent(new C_PacketFailedSendEvent(this, packet, null));
        } catch (IOException | ClassNotFoundException exception) {
            if (logger == null) System.err.println("Failed to send packet: " + exception.getMessage());
            else logger.exception("Failed to connect to send packet", exception);

            eventManager.executeEvent(new C_PacketFailedSendEvent(this, packet, exception));
        }

        return false;
    }

    private void receive() {
        if (!isConnected()) return;

        while (isConnected()) {
            try {
                Object received = inputStream.readObject();

                if (received instanceof Integer) {
                    int id = (Integer) received;
                    Packet packet = packetHandler.getPacketByID(id);
                    if (packetHandler.handlePacket(id, packet, inputStream))
                        eventManager.executeEvent(new C_PacketReceivedEvent(this, packet));
                    else eventManager.executeEvent(new C_PacketFailedReceivedEvent(this, packet, null));
                } else eventManager.executeEvent(new C_UnknownObjectReceivedEvent(this, received));
            } catch (SocketException ignored) {
                disconnect(false);
                return;
            } catch (IOException | ClassNotFoundException exception) {
                if (logger == null) System.err.println("Receive thread failed: " + exception.getMessage());
                else logger.exception("Receive thread failed", exception);

                eventManager.executeEvent(new C_ReceiveThreadFailedEvent(this, exception));
            }
        }

        disconnect(false);
    }
}
