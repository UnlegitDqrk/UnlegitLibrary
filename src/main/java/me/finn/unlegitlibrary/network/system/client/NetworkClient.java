package me.finn.unlegitlibrary.network.system.client;

import me.finn.unlegitlibrary.event.EventManager;
import me.finn.unlegitlibrary.network.system.client.events.*;
import me.finn.unlegitlibrary.network.system.packets.Packet;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;
import me.finn.unlegitlibrary.network.system.packets.impl.ClientIDPacket;
import me.finn.unlegitlibrary.network.system.server.ConnectionHandler;
import me.finn.unlegitlibrary.network.utils.PemUtils;
import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;
import me.finn.unlegitlibrary.utils.Logger;

import javax.net.ssl.*;
import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;

public final class NetworkClient {
    private final String host;
    private final int port;
    private final PacketHandler packetHandler;
    private final EventManager eventManager;
    private final Logger logger;
    private final int timeout;
    private SSLSocket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private final SSLSocketFactory sslSocketFactory;
    private final SSLParameters sslParameters;
    private int clientID = -1;
    private final Proxy proxy;

    public Proxy getProxy() {
        return proxy;
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        if (this.clientID == -1) this.clientID = clientID;
    }

    private final Thread receiveThread = new Thread(this::receive);

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public ObjectInputStream getInputStream() {
        return inputStream;
    }

    public SSLSocket getSocket() {
        return socket;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
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

    private NetworkClient(String host, int port, PacketHandler packetHandler,
                          EventManager eventManager, Logger logger,
                          int timeout, SSLSocketFactory sslSocketFactory,
                          SSLParameters sslParameters, Proxy proxy) {
        this.host = host;
        this.port = port;
        this.packetHandler = packetHandler;
        this.eventManager = eventManager;
        this.logger = logger;
        this.timeout = timeout;
        this.sslSocketFactory = sslSocketFactory;
        this.sslParameters = sslParameters;

        this.packetHandler.setClientInstance(this);
        this.packetHandler.registerPacket(new ClientIDPacket());
        this.proxy = proxy;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed()
                && receiveThread.isAlive() && !receiveThread.isInterrupted();
    }

    public synchronized boolean connect() throws ConnectException {
        if (isConnected()) return false;

        if (logger != null) logger.info("Trying to connect to " + host + ":" + port + "...");
        else System.out.println("Trying to connect to " + host + ":" + port + "...");

        try {
            if (sslSocketFactory == null) throw new ConnectException("SSL socket factory not set. Client certificate required!");

            if (proxy != null) {
                Socket rawSocket = new Socket(proxy);
                rawSocket.connect(new InetSocketAddress(host, port), timeout);
                socket = (SSLSocket) sslSocketFactory.createSocket(rawSocket, host, port, true);
            } else socket = (SSLSocket) sslSocketFactory.createSocket(host, port);

            if (sslParameters != null) socket.setSSLParameters(sslParameters);
            else {
                SSLParameters defaultParams = socket.getSSLParameters();
                defaultParams.setProtocols(new String[]{"TLSv1.3"});
                socket.setSSLParameters(defaultParams);
            }

            socket.setTcpNoDelay(true);
            socket.setSoTimeout(timeout);
            try {
                socket.startHandshake();
            } catch (Exception handshakeEx) {
                throw new ConnectException("Handshake failed: " + handshakeEx.getMessage());
            }

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

            receiveThread.start();
            eventManager.executeEvent(new ClientConnectedEvent(this));
            if (logger != null) logger.info("Connected to " + host + ":" + port);
            else System.out.println("Connected to " + host + ":" + port);
            return true;
        } catch (Exception e) {
            throw new ConnectException("Failed to connect: " + e.getMessage());
        }
    }

    private void receive() {
        try {
            while (isConnected()) {
                Object received = inputStream.readObject();
                handleReceived(received);
            }
        } catch (Exception e) { disconnect(); }
    }

    private void handleReceived(Object received) throws IOException, ClassNotFoundException {
        if (received instanceof Integer id) {
            if (packetHandler.isPacketIDRegistered(id)) {
                Packet packet = packetHandler.getPacketByID(id);
                boolean handled = packetHandler.handlePacket(id, packet, inputStream);
                if (handled) eventManager.executeEvent(new C_PacketReceivedEvent(this, packet));
                else eventManager.executeEvent(new C_PacketReceivedFailedEvent(this, packet));
            } else eventManager.executeEvent(new C_UnknownObjectReceivedEvent(this, received));
        } else eventManager.executeEvent(new C_UnknownObjectReceivedEvent(this, received));
    }

    public synchronized boolean disconnect() {
        if (!isConnected()) return false;
        try {
            receiveThread.interrupt();
            if (outputStream != null) outputStream.close();
            if (inputStream != null) inputStream.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            if (logger != null) logger.exception("Error closing connection", e);
            else System.err.println("Error closing connection: " + e.getMessage());
        } finally {
            socket = null;
            outputStream = null;
            inputStream = null;
            clientID = -1;
            eventManager.executeEvent(new ClientDisconnectedEvent(this));
        }

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NetworkClient target)) return false;
        return target.getClientID() == clientID;
    }

    public boolean sendPacket(Packet packet) throws IOException, ClassNotFoundException {
        if (!isConnected()) return false;
        boolean sent = packetHandler.sendPacket(packet, outputStream);
        if (sent) eventManager.executeEvent(new C_PacketSendEvent(this, packet));
        else eventManager.executeEvent(new C_PacketSendFailedEvent(this, packet));
        return sent;
    }

    // --- Builder ---
    public static class ClientBuilder extends DefaultMethodsOverrider {
        private String host;
        private int port;
        private PacketHandler packetHandler;
        private EventManager eventManager;
        private Logger logger;
        private int timeout = 5000;
        private SSLSocketFactory sslSocketFactory;
        private SSLParameters sslParameters;
        private File caFolder;
        private File clientFolder;
        private File keyFolder;
        private Proxy proxy;

        public ClientBuilder setHost(String host) { this.host = host; return this; }
        public ClientBuilder setPort(int port) { this.port = port; return this; }
        public ClientBuilder setPacketHandler(PacketHandler handler) { this.packetHandler = handler; return this; }
        public ClientBuilder setEventManager(EventManager manager) { this.eventManager = manager; return this; }
        public ClientBuilder setLogger(Logger logger) { this.logger = logger; return this; }
        public ClientBuilder setTimeout(int timeout) { this.timeout = timeout; return this; }
        public ClientBuilder setSSLSocketFactory(SSLSocketFactory factory) { this.sslSocketFactory = factory; return this; }
        public ClientBuilder setSSLParameters(SSLParameters params) { this.sslParameters = params; return this; }
        public ClientBuilder setRootCAFolder(File folder) { this.caFolder = folder; return this; }
        public ClientBuilder setClientCertificatesFolder(File clientFolder, File keyFolder) { this.clientFolder = clientFolder; this.keyFolder = keyFolder; return this; }
        public ClientBuilder setProxy(Proxy proxy) { this.proxy = proxy; return this; }

        public NetworkClient build() {
            if (sslSocketFactory == null && caFolder != null) {
                try { sslSocketFactory = createSSLSocketFactory(caFolder, clientFolder, keyFolder); }
                catch (Exception e) { throw new RuntimeException("Failed to create SSLFactory", e); }
            }

            return new NetworkClient(host, port, packetHandler, eventManager, logger,
                    timeout, sslSocketFactory, sslParameters, proxy);
        }

        public static SSLSocketFactory createSSLSocketFactory(File caFolder, File clientCertFolder, File clientKeyFolder) throws Exception {
            // TrustStore (Root-CAs)
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            int caIndex = 1;
            for (File caFile : caFolder.listFiles((f) -> f.getName().endsWith(".pem"))) {
                try (FileInputStream fis = new FileInputStream(caFile)) {
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    java.security.cert.Certificate caCert = cf.generateCertificate(fis);
                    trustStore.setCertificateEntry("ca" + (caIndex++), caCert);
                }
            }

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(null, null); // Kein Passwort nötig

            int clientIndex = 1;
            for (File certFile : clientCertFolder.listFiles((f) -> f.getName().endsWith(".crt"))) {
                String baseName = certFile.getName().replace(".crt", "");
                File keyFile = new File(clientKeyFolder, baseName + ".key");
                if (!keyFile.exists()) continue;

                java.security.PrivateKey key = PemUtils.loadPrivateKey(keyFile);
                java.security.cert.Certificate cert = PemUtils.loadCertificate(certFile);

                keyStore.setKeyEntry("client" + (clientIndex++), key, null, new java.security.cert.Certificate[]{cert});
            }

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, null);

            // SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            return sslContext.getSocketFactory();
        }
    }
}
