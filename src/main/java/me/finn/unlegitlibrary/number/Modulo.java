/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.number;

import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

public class Modulo extends DefaultMethodsOverrider {

    public static int calculate(int number, int dividedBy) {
        return number % dividedBy;
    }

    public static float calculate(float number, float dividedBy) {
        return number % dividedBy;
    }

    public static double calculate(double number, double dividedBy) {
        return number % dividedBy;
    }

    public static long calculate(long number, long dividedBy) {
        return number % dividedBy;
    }

}
