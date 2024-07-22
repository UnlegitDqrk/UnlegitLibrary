/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.addon.impl;

public class AddonInfo {

    private final String name;
    private final String version;
    private final String author;

    public AddonInfo(String name, String version, String author) {
        this.name = name;
        this.version = version;
        this.author = author;
    }

    public final String getAuthor() {
        return author;
    }

    public final String getName() {
        return name;
    }

    public final String getVersion() {
        return version;
    }

    @Override
    protected AddonInfo clone() throws CloneNotSupportedException {
        return new AddonInfo(name, version, author);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AddonInfo)) return false;
        AddonInfo other = (AddonInfo) obj;
        return other.name.equalsIgnoreCase(name) && other.version.equalsIgnoreCase(version) && other.author.equalsIgnoreCase(author);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
