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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager extends DefaultMethodsOverrider {

    private final HashMap<Class<? extends Event>, HashMap<EventPriority, HashMap<Object, Method>>> registeredListener = new HashMap<>();
    private final HashMap<EventListener, Object> eventListeners = new HashMap<>();

    public final void registerListener(EventListener listenerClass) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (isListenerRegistered(listenerClass)) return;

        for (Method method : listenerClass.getClass().getDeclaredMethods()) {
            Listener listener = method.getAnnotation(Listener.class);

            if (listener == null) continue;

            if (method.getParameterCount() == 1) {
                Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];

                HashMap<EventPriority, HashMap<Object, Method>> list = registeredListener.getOrDefault(eventClass, new HashMap<>());
                HashMap<Object, Method> set = list.getOrDefault(listener.priority(), new HashMap<>());

                set.put(listenerClass, method);
                list.put(listener.priority(), set);
                registeredListener.put(eventClass, list);
            }
        }

        eventListeners.put(listenerClass, listenerClass);
    }

    public synchronized final void unregisterListener(EventListener listenerClass) {
        if (!isListenerRegistered(listenerClass)) return;

        Object clazz = eventListeners.get(listenerClass);

        synchronized (registeredListener) {
            List<Class<? extends Event>> eventsToRemove = new ArrayList<>();

            for (Map.Entry<Class<? extends Event>, HashMap<EventPriority, HashMap<Object, Method>>> entry : registeredListener.entrySet()) {
                Class<? extends Event> eventClass = entry.getKey();
                HashMap<EventPriority, HashMap<Object, Method>> priorityMap = entry.getValue();

                if (priorityMap != null) {
                    synchronized (priorityMap) {
                        List<EventPriority> prioritiesToRemove = new ArrayList<>();

                        for (Map.Entry<EventPriority, HashMap<Object, Method>> priorityEntry : priorityMap.entrySet()) {
                            EventPriority priority = priorityEntry.getKey();
                            HashMap<Object, Method> listeners = priorityEntry.getValue();

                            if (listeners != null) {
                                listeners.remove(clazz);
                                if (listeners.isEmpty()) {
                                    prioritiesToRemove.add(priority);
                                }
                            }
                        }

                        for (EventPriority priority : prioritiesToRemove) {
                            priorityMap.remove(priority);
                        }

                        if (priorityMap.isEmpty()) {
                            eventsToRemove.add(eventClass);
                        }
                    }
                }
            }

            for (Class<? extends Event> eventClass : eventsToRemove) {
                registeredListener.remove(eventClass);
            }
        }

        eventListeners.remove(listenerClass);
    }

    public final boolean isListenerRegistered(EventListener listenerClass) {
        return eventListeners.containsKey(listenerClass);
    }

    public final void executeEvent(Event event) {
        HashMap<EventPriority, HashMap<Object, Method>> list = registeredListener.getOrDefault(event.getClass(), new HashMap<>());

        list.getOrDefault(EventPriority.LOWEST, new HashMap<>()).forEach((k, v) -> {
            if (!isListenerRegistered((EventListener) k)) return;

            try {
                v.invoke(k, event);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                exception.printStackTrace();
            }
        });

        list.getOrDefault(EventPriority.LOW, new HashMap<>()).forEach((k, v) -> {
            if (!isListenerRegistered((EventListener) k)) return;

            try {
                v.invoke(k, event);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                exception.printStackTrace();
            }
        });

        list.getOrDefault(EventPriority.NORMAL, new HashMap<>()).forEach((k, v) -> {
            if (!isListenerRegistered((EventListener) k)) return;

            try {
                v.invoke(k, event);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                exception.printStackTrace();
            }
        });

        list.getOrDefault(EventPriority.HIGH, new HashMap<>()).forEach((k, v) -> {
            if (!isListenerRegistered((EventListener) k)) return;

            try {
                v.invoke(k, event);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                exception.printStackTrace();
            }
        });

        list.getOrDefault(EventPriority.HIGHEST, new HashMap<>()).forEach((k, v) -> {
            if (!isListenerRegistered((EventListener) k)) return;

            try {
                v.invoke(k, event);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                exception.printStackTrace();
            }
        });
    }
}