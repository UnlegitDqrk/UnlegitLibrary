package me.finn.unlegitlibrary.network.system.server;

import me.finn.unlegitlibrary.network.system.packets.Packet;
import me.finn.unlegitlibrary.network.system.packets.impl.ClientIDPacket;
import me.finn.unlegitlibrary.network.system.server.events.*;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.security.cert.X509Certificate;

public class ConnectionHandler {
    private SSLSocket socket;
    private int clientID;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private final NetworkServer server;

    public SSLSocket getSocket() {
        return socket;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public ObjectInputStream getInputStream() {
        return inputStream;
    }

    public NetworkServer getServer() {
        return server;
    }

    public ConnectionHandler(NetworkServer server, SSLSocket socket, int clientID) throws IOException, ClassNotFoundException {
        this.server = server; this.socket = socket; this.clientID = clientID;

        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());

        receiveThread.start();

        sendPacket(new ClientIDPacket());
        server.getEventManager().executeEvent(new ConnectionHandlerConnectedEvent(this));
    }

    public final Thread receiveThread = new Thread(this::receive);

    public int getClientID() { return clientID; }
    public boolean isConnected() { return socket != null && socket.isConnected() && !socket.isClosed() && receiveThread.isAlive(); }

    public synchronized boolean disconnect() {
        if (!isConnected()) return false;
        if (receiveThread.isAlive()) receiveThread.interrupt();

        try { outputStream.close(); inputStream.close(); socket.close(); } catch (IOException ignored) {}
        socket = null; outputStream = null; inputStream = null; clientID = -1;

        server.getConnectionHandlers().remove(this);
        server.getEventManager().executeEvent(new ConnectionHandlerDisconnectedEvent(this));

        return true;
    }

    public boolean sendPacket(Packet packet) throws IOException, ClassNotFoundException {
        if (!isConnected()) return false;
        boolean sent = server.getPacketHandler().sendPacket(packet, outputStream);

        if (sent) server.getEventManager().executeEvent(new S_PacketSendEvent(packet, this));
        else server.getEventManager().executeEvent(new S_PacketSendFailedEvent(packet, this));

        return sent;
    }

    private void receive() {
        while (isConnected()) {
            try {
                Object received = inputStream.readObject();
                if (received instanceof Integer) {
                    int id = (Integer) received;

                    if (server.getPacketHandler().isPacketIDRegistered(id)) {
                        Packet packet = server.getPacketHandler().getPacketByID(id);

                        if (server.getPacketHandler().handlePacket(id, packet, inputStream)) server.getEventManager().executeEvent(new S_PacketReceivedEvent(this, packet));
                        else server.getEventManager().executeEvent(new S_PacketReceivedFailedEvent(this, packet));
                    } else server.getEventManager().executeEvent(new S_UnknownObjectReceivedEvent(received, this));
                } else server.getEventManager().executeEvent(new S_UnknownObjectReceivedEvent(received, this));
            } catch (SocketException se) { disconnect(); }
            catch (Exception ex) {
                if (server.getLogger() != null) server.getLogger().exception("Receive thread exception for client " + clientID, ex);
                else System.err.println("Receive thread exception for client " + clientID + ": " + ex.getMessage());
                disconnect();
            }
        }
    }
}