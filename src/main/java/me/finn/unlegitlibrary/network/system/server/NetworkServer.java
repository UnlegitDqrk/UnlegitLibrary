/*
 * Copyright (C) 2025 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.network.system.server;

import me.finn.unlegitlibrary.event.EventManager;
import me.finn.unlegitlibrary.network.system.packets.Packet;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;
import me.finn.unlegitlibrary.network.system.packets.impl.ClientIDPacket;
import me.finn.unlegitlibrary.network.system.server.events.IncomingConnectionEvent;
import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;
import me.finn.unlegitlibrary.utils.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class NetworkServer {
    private final int port;
    private final PacketHandler packetHandler;
    private final EventManager eventManager;
    private final Logger logger;
    private final int timeout;
    private final int maxRestartAttempts;
    private final int restartDelay;
    private final List<ConnectionHandler> connectionHandlers = new ArrayList<>();
    private int currentAttempts;
    private ServerSocket serverSocket;
    private NetworkServer(int port, PacketHandler packetHandler, EventManager eventManager, Logger logger, int maxRestartAttempts, int restartDelay, int timeout) {
        this.port = port;
        this.timeout = timeout;

        this.packetHandler = packetHandler;
        this.eventManager = eventManager;
        this.logger = logger;

        this.maxRestartAttempts = maxRestartAttempts;
        this.restartDelay = restartDelay;
        this.currentAttempts = 0;

        this.packetHandler.setServerInstance(this);
        this.packetHandler.registerPacket(new ClientIDPacket());
    }    public final Thread incomingConnectionThread = new Thread(this::incomingConnection);

    public Logger getLogger() {
        return logger;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public int getPort() {
        return port;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public List<ConnectionHandler> getConnectionHandlers() {
        return connectionHandlers;
    }

    public boolean isAutoRestartEnabled() {
        return maxRestartAttempts != 0;
    }

    public boolean isRunning() {
        return serverSocket != null && !serverSocket.isClosed() && serverSocket.isBound() &&
                incomingConnectionThread.isAlive() && !incomingConnectionThread.isInterrupted();
    }

    public ConnectionHandler getConnectionHandlerByID(int clientID) {
        for (ConnectionHandler connectionHandler : connectionHandlers)
            if (connectionHandler.getClientID() == clientID) return connectionHandler;
        return null;
    }

    public synchronized boolean start() {
        if (isRunning()) return false;

        if (logger == null) System.out.println("Trying to start on port " + port + "...");
        else logger.info("Trying to start on port " + port + "...");

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(timeout);

            incomingConnectionThread.start();

            if (currentAttempts == 0) currentAttempts++;
            if (logger == null) System.out.println("Started at port " + port + " (Attempts: " + currentAttempts + ")");
            else logger.info("Started at port " + port + " (Attempts: " + currentAttempts + ")");

            currentAttempts = 0;
            return true;
        } catch (IOException exception) {
            if (maxRestartAttempts != 0) {
                try {
                    Thread.sleep(restartDelay);
                } catch (InterruptedException sleepThreadException) {
                    if (logger == null) System.err.println("Restart exception: " + sleepThreadException.getMessage());
                    else logger.exception("Restart exception", sleepThreadException);
                }

                currentAttempts++;
                if (currentAttempts <= maxRestartAttempts || maxRestartAttempts < 0) return start();
            }

            if (logger == null) System.err.println("Failed to start on port " + port + ": " + exception.getMessage());
            else logger.exception("Failed to start on port " + port, exception);
        }

        return false;
    }

    public boolean broadcastPacket(Packet packet) {
        AtomicBoolean toReturn = new AtomicBoolean(false);
        connectionHandlers.forEach(connectionHandler -> {
            try {
                if (!toReturn.get()) return;
                toReturn.set(connectionHandler.sendPacket(packet));
            } catch (IOException | ClassNotFoundException e) {
                toReturn.set(false);
            }
        });

        return toReturn.get();
    }

    private void incomingConnection() {
        if (!isRunning()) return;

        try {
            while (isRunning()) {
                Socket socket = serverSocket.accept();
                socket.setTcpNoDelay(true);
                socket.setSoTimeout(timeout);

                if (logger == null) System.out.println("Accepted connection from " + socket.getRemoteSocketAddress());
                else logger.info("Accepted connection from " + socket.getRemoteSocketAddress());

                IncomingConnectionEvent incomingConnectionEvent = new IncomingConnectionEvent(this, socket);

                eventManager.executeEvent(incomingConnectionEvent);

                if (incomingConnectionEvent.isCancelled()) {
                    socket.close();
                    return;
                }

                ConnectionHandler connectionHandler = new ConnectionHandler(this, socket, connectionHandlers.size() + 1);
                connectionHandlers.add(connectionHandler);
            }
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public boolean sendPacket(int clientID, Packet packet) throws IOException, ClassNotFoundException {
        return getConnectionHandlerByID(clientID).sendPacket(packet);
    }

    public boolean sendPacket(Packet packet, int clientID) throws IOException, ClassNotFoundException {
        return sendPacket(clientID, packet);
    }

    public static class ServerBuilder extends DefaultMethodsOverrider {
        private int port;

        private PacketHandler packetHandler;
        private EventManager eventManager;
        private Logger logger;

        private int maxRestartAttempts = 0;
        private int restartDelay = 3000;
        private int timeout = 0;

        public final NetworkServer build() {
            return new NetworkServer(port, packetHandler, eventManager, logger, maxRestartAttempts, restartDelay, timeout);
        }

        public final ServerBuilder setEventManager(EventManager eventManager) {
            this.eventManager = eventManager;
            return this;
        }

        public final ServerBuilder setLogger(Logger logger) {
            this.logger = logger;
            return this;
        }

        public final ServerBuilder setMaxReconnectAttempts(int maxRestartAttempts) {
            this.maxRestartAttempts = maxRestartAttempts;
            return this;
        }

        public final ServerBuilder setPacketHandler(PacketHandler packetHandler) {
            this.packetHandler = packetHandler;
            return this;
        }

        public final ServerBuilder setPort(int port) {
            this.port = port;
            return this;
        }

        public final ServerBuilder setReconnectDelay(int reconnectDelay) {
            this.restartDelay = reconnectDelay;
            return this;
        }

        public final ServerBuilder setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }
    }


}
