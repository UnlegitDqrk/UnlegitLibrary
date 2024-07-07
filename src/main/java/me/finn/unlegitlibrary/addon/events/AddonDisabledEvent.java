/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.addon.events;

import me.finn.unlegitlibrary.addon.impl.Addon;
import me.finn.unlegitlibrary.event.impl.Event;
import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

public class AddonDisabledEvent extends Event {

    public final Addon addon;

    public AddonDisabledEvent(Addon addon) {
        this.addon = addon;
    }

    @Override
    protected final Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public final String toString() {
        return super.toString();
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }
}
