/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.network.system.server;

import me.finn.unlegitlibrary.event.EventManager;
import me.finn.unlegitlibrary.network.system.packets.Packet;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;
import me.finn.unlegitlibrary.network.system.server.events.server.S_StartedEvent;
import me.finn.unlegitlibrary.network.system.server.events.server.S_StoppedEvent;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class NetworkServer {

    private final int port;
    private final PacketHandler packetHandler;
    private final EventManager eventManager;
    private final boolean autoRestart;
    private final boolean debugLog;
    private final int maxAttempts;
    private final int attemptDelayInSec;
    private final List<ClientHandler> clientHandlers = new ArrayList<>();

    private ServerSocket serverSocket;
    private int attempt = 1;
    private final Thread incomingConnectionThread = new Thread(this::incomingConnection);

    private NetworkServer(int port, PacketHandler packetHandler, EventManager eventManager, boolean autoRestart, boolean debugLog, int maxAttempts, int attemptDelayInSec) {
        this.port = port;

        this.packetHandler = packetHandler;
        this.eventManager = eventManager;

        this.autoRestart = autoRestart;
        this.debugLog = debugLog;

        this.maxAttempts = maxAttempts;
        this.attemptDelayInSec = attemptDelayInSec;
    }

    public final int getPort() {
        return port;
    }

    public final PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public final boolean isAutoRestart() {
        return autoRestart;
    }

    public final boolean isDebugLogEnabled() {
        return debugLog;
    }

    public final ServerSocket getServerSocket() {
        return serverSocket;
    }

    public final List<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    public final Thread getIncomingConnectionThread() {
        return incomingConnectionThread;
    }

    public final boolean isRunning() {
        return serverSocket != null && !serverSocket.isClosed() && serverSocket.isBound() &&
                incomingConnectionThread.isAlive() && !incomingConnectionThread.isInterrupted();
    }

    public synchronized final void start() throws IOException, InterruptedException {
        if (isRunning()) return;
        if (debugLog) System.out.println("Starting server...");

        try {
            clientHandlers.clear();

            serverSocket = new ServerSocket(port);
            incomingConnectionThread.start();

            attempt = 1;
            eventManager.executeEvent(new S_StartedEvent(this));

            if (debugLog) System.out.println("Server started on port " + port + ". Attempts: " + attempt);
        } catch (BindException exception) {
            if (autoRestart) {
                if (attempt > maxAttempts) throw exception;
                if (debugLog) System.out.println("Failed to start! Retrying... (Attempt: " + attempt++ + ")");

                Thread.sleep(attemptDelayInSec * 1000L);
                start();
            } else throw exception;
        }
    }

    public synchronized final void stop() throws IOException {
        if (!isRunning()) return;
        if (debugLog) System.out.println("Stopping server...");

        clientHandlers.forEach(clientHandler -> {
            try {
                clientHandler.disconnect();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });

        clientHandlers.clear();

        serverSocket.close();
        incomingConnectionThread.interrupt();

        serverSocket = null;

        eventManager.executeEvent(new S_StoppedEvent(this));
        if (debugLog) System.out.println("Server stopped.");
    }

    public final boolean sendPacket(ClientHandler clientHandler, Packet packet) throws IOException, ClassNotFoundException {
        return clientHandler.sendPacket(packet);
    }

    private final void incomingConnection() {
        if (!isRunning()) return;

        try {
            while (isRunning()) {
                Socket socket = serverSocket.accept();
                if (socket == null) continue;

                socket.setTcpNoDelay(false);
                if (debugLog) System.out.println("New incoming connection...");
                clientHandlers.add(new ClientHandler(this, socket, clientHandlers.size() + 1));
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public final ClientHandler getClientHandlerByID(int id) {
        for (ClientHandler clientHandler : clientHandlers) if (clientHandler.getClientID() == id) return clientHandler;
        return null;
    }

    public final EventManager getEventManager() {
        return eventManager;
    }

    public static class ServerBuilder {
        private int port;
        private boolean autoRestart = false;
        private boolean debugLog = false;
        private PacketHandler packetHandler = new PacketHandler();
        private EventManager eventManager = new EventManager();
        private int maxAttempts = 10;
        private int attemptDelayInSec = 1;

        public final ServerBuilder enableAutoRestart() {
            this.autoRestart = true;
            return this;
        }

        public final ServerBuilder enableDebugLog() {
            this.debugLog = true;
            return this;
        }

        public final ServerBuilder setPort(int port) {
            this.port = port;
            return this;
        }

        public final ServerBuilder setEventManager(EventManager eventManager) {
            this.eventManager = eventManager;
            return this;
        }

        public final ServerBuilder setPacketHandler(PacketHandler packetHandler) {
            this.packetHandler = packetHandler;
            return this;
        }

        public final ServerBuilder setAttemptDelayInSeconds(int attemptDelayInSec) {
            this.attemptDelayInSec = attemptDelayInSec;
            return this;
        }

        public final ServerBuilder setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
            return this;
        }

        public final NetworkServer build() {
            return new NetworkServer(port, packetHandler, eventManager, autoRestart, debugLog, maxAttempts, attemptDelayInSec);
        }
    }


}
