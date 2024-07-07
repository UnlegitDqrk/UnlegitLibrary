package me.finn.unlegitlibrary.network.system;

import me.finn.unlegitlibrary.event.EventManager;
import me.finn.unlegitlibrary.network.system.packets.PacketHandler;
import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

public abstract class NetworkPipeline extends DefaultMethodsOverrider {

    public int port = 3982;
    public PacketHandler packetHandler = new PacketHandler();
    public EventManager eventManager = new EventManager();
    public boolean logDebug = false;
    public int maxAttempts = 10; // -1 = Endless | 0 = Disabled
    public int attemptDelayInSeconds = 5;

    public abstract void implement();
}
