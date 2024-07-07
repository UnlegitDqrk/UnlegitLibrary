/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.number;

import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

public class NumberConversions extends DefaultMethodsOverrider {
    public static int floor(double num) {
        int floor = (int) num;
        return (double) floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }

    public static int ceil(double num) {
        int floor = (int) num;
        return (double) floor == num ? floor : floor + (int) (~Double.doubleToRawLongBits(num) >>> 63);
    }

    public static int round(double num) {
        return floor(num + 0.5D);
    }

    public static double square(double num) {
        return num * num;
    }

    public static boolean isFinite(double d) {
        return Math.abs(d) <= 1.7976931348623157E308D;
    }

    public static boolean isFinite(float f) {
        return Math.abs(f) <= 3.4028235E38F;
    }

    public static void checkFinite(double d, String message) {
        if (!isFinite(d)) throw new IllegalArgumentException(message);
    }

    public static void checkFinite(float d, String message) {
        if (!isFinite(d)) throw new IllegalArgumentException(message);
    }
}
