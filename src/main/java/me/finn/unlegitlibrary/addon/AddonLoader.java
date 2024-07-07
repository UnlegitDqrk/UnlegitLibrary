/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.addon;

import me.finn.unlegitlibrary.addon.impl.Addon;
import me.finn.unlegitlibrary.event.EventListener;
import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

public class AddonLoader extends DefaultMethodsOverrider {
    private final List<Addon> addons;
    private final Map<String, Class<?>> loadedClasses;

    public AddonLoader() {
        this.addons = new ArrayList<>();
        this.loadedClasses = new HashMap<>();
    }

    public final void loadAddonsFromDirectory(File addonFolder) throws IOException {
        if (!addonFolder.exists()) return;
        if (!addonFolder.isDirectory()) return;

        File[] files = addonFolder.listFiles((d, name) -> name.toLowerCase().endsWith(".jar"));
        if (files != null) {
            for (File file : files) loadAddonFromJar(file);
        }
    }

    public final void loadAddonFromJar(File file) throws IOException {
        try (JarFile jarFile = new JarFile(file)) {
            URL[] urls = {new URL("jar:file:" + file.getAbsolutePath() + "!/")};
            URLClassLoader classLoader = URLClassLoader.newInstance(urls, getClass().getClassLoader());

            jarFile.stream().forEach(jarEntry -> {
                if (jarEntry.getName().endsWith(".class")) {
                    String className = jarEntry.getName().replace('/', '.').replace(".class", "");

                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        loadedClasses.put(className, clazz);

                        if (Addon.class.isAssignableFrom(clazz)) {
                            Addon addon = (Addon) clazz.getDeclaredConstructor().newInstance();
                            addons.add(addon);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            });
        }
    }

    public final void enableAddon(Addon addon) {
        if (!addons.contains(addon)) return;
        addon.enable();
    }

    public final void disableAddon(Addon addon) {
        if (!addons.contains(addon)) return;
        addon.disable();
    }

    public final void enableAll() {
        addons.forEach(this::enableAddon);
    }

    public final void disableAll() {
        addons.forEach(this::disableAddon);
    }

    public final void registerEventListener(Addon addon, Class<? extends EventListener> eventListener) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if (!addons.contains(addon)) return;
        addon.registerEventListener(eventListener);
    }

    public final void unregisterEventListener(Addon addon, Class<? extends EventListener> eventListener) {
        if (!addons.contains(addon)) return;
        addon.unregisterEventListener(eventListener);
    }

    public final List<Addon> getLoadedAddons() {
        return new ArrayList<>(addons);
    }
}
