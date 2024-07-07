/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.file;

import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class ClassDefiner extends DefaultMethodsOverrider {

    private static final Map<ClassLoader, ClassDefinerLoader> loaders = Collections.synchronizedMap(new WeakHashMap<>());

    public static <T> Class<T> define(final Class<?> parent, final String name, final byte[] data) {
        return define(parent.getClassLoader(), name, data);
    }

    public static <T> Class<T> define(final ClassLoader parentLoader, final String name, final byte[] data) {
        ClassDefinerLoader loader = loaders.computeIfAbsent(parentLoader, ClassDefinerLoader::new);

        synchronized (loader.getClassLoadingLock(name)) {
            if (loader.hasClass(name)) throw new IllegalStateException(name + " already defined");
            return (Class<T>) loader.define(name, data);
        }
    }

    private static class ClassDefinerLoader extends ClassLoader {
        static {
            ClassLoader.registerAsParallelCapable();
        }

        protected ClassDefinerLoader(final ClassLoader parent) {
            super(parent);
        }

        private final Class<?> define(final String name, final byte[] data) {
            synchronized (this.getClassLoadingLock(name)) {
                Class<?> c = this.defineClass(name, data, 0, data.length);
                this.resolveClass(c);
                return c;
            }
        }

        @Override
        public final Object getClassLoadingLock(final String name) {
            return super.getClassLoadingLock(name);
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

        public boolean hasClass(final String name) {
            synchronized (this.getClassLoadingLock(name)) {
                try {
                    Class.forName(name);
                    return true;
                } catch (ClassNotFoundException exception) {
                    return false;
                }
            }
        }
    }
}
