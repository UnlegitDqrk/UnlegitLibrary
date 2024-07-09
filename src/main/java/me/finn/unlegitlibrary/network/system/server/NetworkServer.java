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
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class NetworkServer {
    private final int port;
    private final boolean debugLog;

    private final PacketHandler packetHandler;
    private final EventManager eventManager;

    private final int maxAttempts;
    private final int attemptDelayInSec;

    private final List<ClientHandler> clientHandlers = new ArrayList<>();
    private final Thread incomingConnectionThread = new Thread(this::incomingConnection);

    private ServerSocket serverSocket;
    private int attempt;

    private NetworkServer(int port, boolean debugLog, PacketHandler packetHandler, EventManager eventManager, int maxAttempts, int attemptDelayInSec) {
        this.port = port;
        this.debugLog = debugLog;

        this.packetHandler = packetHandler;
        this.eventManager = eventManager;

        this.maxAttempts = maxAttempts;
        this.attemptDelayInSec = attemptDelayInSec;
        this.attempt = 1;
    }

    public final int getPort() {
        return port;
    }

    public final PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public final boolean isAutoRestartEnabled() {
        return maxAttempts != 0 && !incomingConnectionThread.isInterrupted();
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
        try {
            if (isRunning()) return;
            if (debugLog) System.out.println("Starting server...");

            clientHandlers.clear();

            serverSocket = new ServerSocket(port);
            incomingConnectionThread.start();

            attempt = 1;
            eventManager.executeEvent(new S_StartedEvent(this));

            if (debugLog) System.out.println("Server started on port " + port + ". Attempts: " + attempt);
        } catch (BindException exception) {
            if (isAutoRestartEnabled()) restart();
            else if (!incomingConnectionThread.isInterrupted()) throw exception;
        }
    }

    public synchronized final void stop() throws IOException {
        if (!isRunning()) return;
        if (debugLog) System.out.println("Stopping server...");

        List<ClientHandler> handlersToDisconnect = new ArrayList<>(clientHandlers);

        handlersToDisconnect.forEach(clientHandler -> {
            try {
                clientHandler.disconnect();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });

        handlersToDisconnect.clear();
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
        try {
            if (!isRunning()) return;

            while (isRunning()) {
                Socket socket = serverSocket.accept();
                if (socket == null) continue;

                socket.setTcpNoDelay(false);
                if (debugLog) System.out.println("New incoming connection...");
                clientHandlers.add(new ClientHandler(this, socket, clientHandlers.size() + 1));
            }
        } catch (SocketException exception) {
            if (isAutoRestartEnabled()) restart();
            else if (!incomingConnectionThread.isInterrupted()) exception.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private final void restart() {
        if (isAutoRestartEnabled()) {
            if (isRunning()) {
                try {
                    stop();
                } catch (IOException exception) {
                    if (maxAttempts > 0 && attempt > maxAttempts) {
                        eventManager.executeEvent(new S_StoppedEvent(this));
                        exception.printStackTrace();
                        return;
                    }
                }
            }

            if (debugLog) System.out.println("Trying to restart... (Attempt: " + attempt++ + ")");

            try {
                Thread.sleep(attemptDelayInSec * 1000L);
                start();
            } catch (InterruptedException | IOException exception) {
                if (maxAttempts == -1) restart();
                else if (attempt <= maxAttempts) restart();
                else {
                    eventManager.executeEvent(new S_StoppedEvent(this));
                    exception.printStackTrace();
                }
            }
        } else {
            try {
                stop();
            } catch (IOException exception) {
                eventManager.executeEvent(new S_StoppedEvent(this));
                exception.printStackTrace();
            }
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
        private boolean debugLog = false;
        private PacketHandler packetHandler = new PacketHandler();
        private EventManager eventManager = new EventManager();
        private int maxAttempts = 0;
        private int attemptDelayInSec = 1;

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
            return new NetworkServer(port, debugLog, packetHandler, eventManager, maxAttempts, attemptDelayInSec);
        }
    }
}
