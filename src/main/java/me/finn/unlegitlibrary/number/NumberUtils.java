/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.number;

import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

public class NumberUtils extends DefaultMethodsOverrider {
    public static int[] toIntArray(Integer[] integers) {
        int[] result = new int[integers.length];
        for (int i = 0; i < integers.length; i++) result[i] = integers[i].intValue();
        return result;
    }
}
