/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.string;

import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.util.ArrayList;
import java.util.List;

public class StringUtils extends DefaultMethodsOverrider {

    public static String removeLastChar(String string) {
        return string.substring(0, string.length() - 1);
    }

    public static String removeCharAtIndex(String string, int index) {
        return string.substring(0, index) + string.substring(index + 1);
    }

    public static String reverseString(String string) {
        return new StringBuilder(string).reverse().toString();
    }

    public static String[] removeEmptyStrings(String[] strings) {
        List<String> result = new ArrayList<>();

        for (int i = 0; i < strings.length; i++) if (!isEmptyString(strings[i])) result.add(strings[i]);

        String[] res = new String[result.size()];
        result.toArray(res);

        return res;
    }

    public static boolean isEmptyString(String string) {
        return string == null || string.isEmpty() || string.trim().isEmpty() || string.equalsIgnoreCase(" ");
    }

    public static String[] removeEmptyStringsExceptWhitespace(String[] strings) {
        List<String> result = new ArrayList<>();

        for (int i = 0; i < strings.length; i++) if (!isEmptyStringExceptWhitespace(strings[i])) result.add(strings[i]);

        String[] res = new String[result.size()];
        result.toArray(res);

        return res;
    }

    public static boolean isEmptyStringExceptWhitespace(String string) {
        if (string == null) return true;
        return (string.isEmpty() || string.trim().isEmpty()) && !string.equalsIgnoreCase(" ");
    }
}
