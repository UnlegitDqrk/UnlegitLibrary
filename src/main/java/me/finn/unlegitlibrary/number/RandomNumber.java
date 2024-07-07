/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.number;

import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.util.SplittableRandom;

public class RandomNumber extends DefaultMethodsOverrider {

    public static final SplittableRandom random = new SplittableRandom();

    public static int random(int min, int max) {
        if (min == max) return max;
        return random.nextInt(max + 1 - min) + min;
    }

    public static double random(double min, double max) {
        if (min == max) return max;
        return min + Math.random() * (max - min);
    }

    public static float random(float min, float max) {
        if (min == max) return max;
        return min + (float) Math.random() * (max - min);
    }
}