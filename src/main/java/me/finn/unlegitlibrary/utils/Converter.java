/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.utils;

public class Converter extends DefaultMethodsOverrider {

    public static String convertToString(Object object) {
        return String.valueOf(object);
    }

    public static boolean convertToBoolean(Object object, boolean fallback) {
        if (object instanceof String) return Boolean.valueOf(convertToString(object));
        else if (object instanceof Double) return Math.round(convertToDouble(object, fallback ? 1 : -1)) >= 1;
        else if (object instanceof Float) return Math.round(convertToFloat(object, fallback ? 1 : -1)) >= 1;
        else if (object instanceof Integer) return convertToInteger(object, fallback ? 1 : -1) >= 1;
        else if (object instanceof Long) return Math.round(convertToLong(object, fallback ? 1 : -1)) >= 1;
        else if (object instanceof Short) return convertToShort(object, (short) (fallback ? 1 : -1)) >= 1;
        else if (object instanceof Byte) return convertToByte(object, (byte) (fallback ? 1 : -1)) >= 0.001;
        else return fallback;
    }

    public static int convertToInteger(Object object, int fallback) {
        try {
            return Integer.parseInt(convertToString(object));
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    public static short convertToShort(Object object, short fallback) {
        try {
            return Short.parseShort(convertToString(object));
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    public static byte convertToByte(Object object, byte fallback) {
        try {
            return Byte.parseByte(convertToString(object));
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    public static long convertToLong(Object object, long fallback) {
        try {
            return Long.parseLong(convertToString(object));
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    public static float convertToFloat(Object object, float fallback) {
        try {
            return Float.parseFloat(convertToString(object));
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    public static double convertToDouble(Object object, double fallback) {
        try {
            return Double.parseDouble(convertToString(object));
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }
}