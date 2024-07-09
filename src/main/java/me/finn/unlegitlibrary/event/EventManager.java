/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.event;

import me.finn.unlegitlibrary.event.impl.Event;
import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class EventManager extends DefaultMethodsOverrider {

    private final HashMap<Class<? extends Event>, HashMap<EventPriority, HashMap<Object, Method>>> registeredListener = new HashMap<>();
    private final HashMap<Class<? extends EventListener>, Object> eventListeners = new HashMap<>();

    public final void registerListener(Class<? extends EventListener> listenerClass) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (isListenerRegistered(listenerClass)) return;

        Object clazz = listenerClass.getDeclaredConstructor().newInstance();

        for (Method method : clazz.getClass().getDeclaredMethods()) {
            Listener listener = method.getAnnotation(Listener.class);

            if (listener == null) continue;

            if (method.getParameterCount() == 1) {
                Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];

                HashMap<EventPriority, HashMap<Object, Method>> list = registeredListener.getOrDefault(eventClass, new HashMap<>());
                HashMap<Object, Method> set = list.getOrDefault(listener.priority(), new HashMap<>());

                set.put(clazz, method);
                list.put(listener.priority(), set);
                registeredListener.put(eventClass, list);
            }
        }

        eventListeners.put(listenerClass, clazz);
    }

    public final void unregisterListener(Class<? extends EventListener> listenerClass) {
        if (!isListenerRegistered(listenerClass)) return;

        Object clazz = eventListeners.get(listenerClass);

        for (Class<? extends Event> eventClass : registeredListener.keySet()) {
            HashMap<EventPriority, HashMap<Object, Method>> priorityMap = registeredListener.get(eventClass);

            if (priorityMap != null) {
                synchronized (priorityMap) {
                    for (EventPriority priority : priorityMap.keySet()) {
                        HashMap<Object, Method> listeners = priorityMap.get(priority);
                        if (listeners != null) {
                            listeners.remove(clazz);
                            if (listeners.isEmpty()) priorityMap.remove(priority);
                        }
                    }

                    if (priorityMap.isEmpty()) registeredListener.remove(eventClass);
                }
            }
        }

        eventListeners.remove(listenerClass);
    }

    public final boolean isListenerRegistered(Class<? extends EventListener> listenerClass) {
        return eventListeners.containsKey(listenerClass);
    }

    public final void executeEvent(Event event) {
        HashMap<EventPriority, HashMap<Object, Method>> list = registeredListener.getOrDefault(event.getClass(), new HashMap<>());

        list.getOrDefault(EventPriority.LOWEST, new HashMap<>()).forEach((k, v) -> {
            if (!isListenerRegistered((Class<? extends EventListener>) k.getClass())) return;

            try {
                v.invoke(k, event);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                exception.printStackTrace();
            }
        });

        list.getOrDefault(EventPriority.LOW, new HashMap<>()).forEach((k, v) -> {
            if (!isListenerRegistered((Class<? extends EventListener>) k.getClass())) return;

            try {
                v.invoke(k, event);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                exception.printStackTrace();
            }
        });

        list.getOrDefault(EventPriority.NORMAL, new HashMap<>()).forEach((k, v) -> {
            if (!isListenerRegistered((Class<? extends EventListener>) k.getClass())) return;

            try {
                v.invoke(k, event);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                exception.printStackTrace();
            }
        });

        list.getOrDefault(EventPriority.HIGH, new HashMap<>()).forEach((k, v) -> {
            if (!isListenerRegistered((Class<? extends EventListener>) k.getClass())) return;

            try {
                v.invoke(k, event);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                exception.printStackTrace();
            }
        });

        list.getOrDefault(EventPriority.HIGHEST, new HashMap<>()).forEach((k, v) -> {
            if (!isListenerRegistered((Class<? extends EventListener>) k.getClass())) return;

            try {
                v.invoke(k, event);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                exception.printStackTrace();
            }
        });
    }
}