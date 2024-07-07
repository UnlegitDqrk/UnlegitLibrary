/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.file;

import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class ReflectUtils extends DefaultMethodsOverrider {

    public static Method getMethodByArgs(final Class<?> clazz, final Class<?>... args) {
        for (Method method : clazz.getDeclaredMethods())
            if (Arrays.equals(method.getParameterTypes(), args)) return method;
        return null;
    }

    public static Field getEnumField(final Enum<?> value) throws IllegalAccessException {
        for (Field field : value.getClass().getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) continue;
            if (!field.getType().equals(value.getClass())) continue;

            field.setAccessible(true);
            if (value.equals(field.get(null))) return field;
        }

        return null;
    }
}