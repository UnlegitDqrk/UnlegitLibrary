package me.finn.unlegitlibrary.network.system.server;

import me.finn.unlegitlibrary.event.EventManager;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;
import me.finn.unlegitlibrary.network.system.packets.impl.ClientIDPacket;
import me.finn.unlegitlibrary.network.system.server.events.IncomingConnectionEvent;
import me.finn.unlegitlibrary.network.utils.PemUtils;
import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;
import me.finn.unlegitlibrary.utils.Logger;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public final class NetworkServer {
    private final int port;
    private final PacketHandler packetHandler;
    private final EventManager eventManager;
    private final Logger logger;
    private final int timeout;
    private SSLServerSocket serverSocket;
    private final SSLServerSocketFactory sslServerSocketFactory;

    private final List<ConnectionHandler> connectionHandlers = new ArrayList<>();
    private final Thread incomingThread = new Thread(this::incomingConnections);

    public List<ConnectionHandler> getConnectionHandlers() {
        return connectionHandlers;
    }

    public ConnectionHandler getConnectionHandlerByID(int clientID) {
        for (ConnectionHandler connectionHandler : connectionHandlers) if (connectionHandler.getClientID() == clientID) return connectionHandler;
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NetworkServer target)) return false;
        return super.equals(obj);
    }

    public int getPort() {
        return port;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public Logger getLogger() {
        return logger;
    }

    public SSLServerSocket getServerSocket() {
        return serverSocket;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    private final boolean requireClientCert;

    private NetworkServer(int port, PacketHandler packetHandler, EventManager eventManager,
                          Logger logger, int timeout, SSLServerSocketFactory factory, boolean requireClientCert) {
        this.port = port; this.packetHandler = packetHandler; this.eventManager = eventManager;
        this.logger = logger; this.timeout = timeout; this.sslServerSocketFactory = factory;

        this.packetHandler.setServerInstance(this);
        this.packetHandler.registerPacket(new ClientIDPacket());
        this.requireClientCert = requireClientCert;
    }

    public boolean start() {
        try {
            serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
            serverSocket.setNeedClientAuth(requireClientCert);
            serverSocket.setSoTimeout(timeout);
            serverSocket.setEnabledProtocols(new String[]{"TLSv1.3"});
            incomingThread.start();
            if (logger != null) logger.log("Server started on port " + port);
            else System.out.println("Server started on port " + port);
            return true;
        } catch (Exception e) {if (logger != null) logger.exception("Failed to start", e);
        else System.err.println("Failed to start: " + e.getMessage()); return false; }
    }

    private void incomingConnections() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                if (!(socket instanceof SSLSocket ssl)) { socket.close(); continue; }
                ssl.setTcpNoDelay(true);
                ssl.setSoTimeout(timeout);
                try { ssl.startHandshake(); }
                catch (Exception handshakeEx) {
                    if (logger != null) logger.exception("Handshake failed", handshakeEx);
                    else System.err.println("Handshake failed: " + handshakeEx.getMessage());
                    ssl.close();
                    continue;
                }

                IncomingConnectionEvent event = new IncomingConnectionEvent(this, ssl);
                eventManager.executeEvent(event);
                if (event.isCancelled()) { ssl.close(); continue; }

                try {
                    ConnectionHandler connectionHandler = new ConnectionHandler(this, ssl, connectionHandlers.size() + 1);
                    connectionHandlers.add(connectionHandler);
                } catch (Exception exception) {
                    ssl.close();
                    continue;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- Builder ---
    public static class ServerBuilder extends DefaultMethodsOverrider {
        private int port;
        private PacketHandler packetHandler;
        private EventManager eventManager;
        private Logger logger;
        private int timeout = 5000;
        private SSLServerSocketFactory factory;
        private boolean requireClientCert;
        private File caFolder;
        private File serverCertFile;
        private File serverKeyFile;

        public ServerBuilder setPort(int port) { this.port = port; return this; }
        public ServerBuilder setPacketHandler(PacketHandler handler) { this.packetHandler = handler; return this; }
        public ServerBuilder setEventManager(EventManager manager) { this.eventManager = manager; return this; }
        public ServerBuilder setLogger(Logger logger) { this.logger = logger; return this; }
        public ServerBuilder setTimeout(int timeout) { this.timeout = timeout; return this; }
        public ServerBuilder setSSLServerSocketFactory(SSLServerSocketFactory factory) { this.factory = factory; return this; }
        public ServerBuilder setRequireClientCertificate(boolean requireClientCertificate) { this.requireClientCert = requireClientCertificate; return this; }
        public ServerBuilder setRootCAFolder(File folder) { this.caFolder = folder; return this; }
        public ServerBuilder setServerCertificate(File certFile, File keyFile) { this.serverCertFile = certFile; this.serverKeyFile = keyFile; return this; }

        public NetworkServer build() {
            if (factory == null && caFolder != null && serverCertFile != null && serverKeyFile != null) {
                try { factory = createSSLServerSocketFactory(caFolder, serverCertFile, serverKeyFile); }
                catch (Exception e) { throw new RuntimeException("Failed to create SSLServerSocketFactory", e); }
            }
            return new NetworkServer(port, packetHandler, eventManager, logger, timeout, factory, requireClientCert);
        }

        public static SSLServerSocketFactory createSSLServerSocketFactory(File caFolder, File serverCert, File serverKey) throws Exception {
            // TrustStore (Root-CAs)
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            int caIndex = 1;
            for (File caFile : caFolder.listFiles((f) -> f.getName().endsWith(".pem"))) {
                try (FileInputStream fis = new FileInputStream(caFile)) {
                    java.security.cert.Certificate cert = PemUtils.loadCertificate(caFile);
                    trustStore.setCertificateEntry("ca" + (caIndex++), cert);
                }
            }

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(null, null);
            java.security.PrivateKey key = PemUtils.loadPrivateKey(serverKey);
            java.security.cert.Certificate cert = PemUtils.loadCertificate(serverCert);
            keyStore.setKeyEntry("server", key, null, new java.security.cert.Certificate[]{cert});

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, null);

            SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            return sslContext.getServerSocketFactory();
        }
    }
}
