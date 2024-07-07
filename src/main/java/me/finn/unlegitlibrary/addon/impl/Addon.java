/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.addon.impl;

import me.finn.unlegitlibrary.addon.events.AddonDisabledEvent;
import me.finn.unlegitlibrary.addon.events.AddonEnabledEvent;
import me.finn.unlegitlibrary.event.EventListener;
import me.finn.unlegitlibrary.event.EventManager;
import me.finn.unlegitlibrary.event.impl.Event;
import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.lang.reflect.InvocationTargetException;

public abstract class Addon {

    private final AddonInfo addonInfo;
    private final EventManager eventManager;
    private boolean isEnabled = false;

    public Addon(AddonInfo addonInfo) {
        this.addonInfo = addonInfo;
        this.eventManager = new EventManager();
    }

    public final boolean isEnabled() {
        return isEnabled;
    }

    public final AddonInfo getAddonInfo() {
        return addonInfo;
    }

    public void executeEvent(Event event) {
        if (!isEnabled) return;
        eventManager.executeEvent(event);
    }

    public final void registerEventListener(Class<? extends EventListener> eventListener) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        eventManager.registerListener(eventListener);
    }

    public final void unregisterEventListener(Class<? extends EventListener> eventListener) {
        eventManager.unregisterListener(eventListener);
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public final void enable() {
        if (isEnabled) return;

        isEnabled = true;
        onEnable();
        eventManager.executeEvent(new AddonEnabledEvent(this));
    }

    public final void disable() {
        if (!isEnabled) return;

        isEnabled = false;
        onDisable();
        eventManager.executeEvent(new AddonDisabledEvent(this));
    }
}
