/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.string.color;

import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MinecraftColorUtils extends DefaultMethodsOverrider {
    public static int clamp_int(int num, int min, int max) {
        return num < min ? min : (Math.min(num, max));
    }

    public static int toRGB(int red, int green, int blue, int alpha) {
        return (alpha & 0xFF) << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
    }

    public static String toColor(String colorChar) {
        return '§' + colorChar;
    }

    public static String booleanToColor(boolean value) {
        return value ? Color.GREEN.toString() : Color.RED.toString();
    }

    public static int getAstolfo(int delay, float offset, float hueSetting) {
        float speed = 500;
        float hue = (float) (System.currentTimeMillis() % delay) + offset;

        while (hue > speed) hue -= speed;
        hue /= speed;

        if (hue > 0.5D) hue = 0.5F - hue - 0.5F;

        hue += hueSetting;
        return Color.HSBtoRGB(hue, 0.5F, 1.0F);
    }

    public static String removeColorCodes(String message) {
        String colorCodes = "0123456789abcdefklmnor";
        ArrayList<String> colors = new ArrayList<String>();

        for (char c : colorCodes.toCharArray()) colors.add("" + c);

        Object object = colors.iterator();

        while (((Scanner) object).hasNext()) {
            String s = ((Scanner) object).next();
            message = message.replaceAll("\u00a7" + s, "");
        }

        return message;
    }

    public static Color rainbowEffect(final long offset, final float fade) {
        final float hue = (System.nanoTime() + offset) / 1.0E10f % 1.0f;
        final long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, 1.0f, 1.0f)), 16);
        final Color c = new Color((int) color);

        return new Color(c.getRed() / 255.0f * fade, c.getGreen() / 255.0f * fade, c.getBlue() / 255.0f * fade, c.getAlpha() / 255.0f);
    }

    public static int rainbowEffect() {
        return Color.HSBtoRGB((float) (System.currentTimeMillis() % 3000L) / 3000.0F, 0.8F, 1.0F);
    }

    public static int chroma(float delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
        rainbowState %= 360;
        return Color.HSBtoRGB((float) (rainbowState / 360.0F), 0.75F, 1.0F);
    }

    public static int rgbColor(int red, int green, int blue) {
        return new Color(red, green, blue).getRGB();
    }

    public static int rainbow(float seconds, float saturation, float brightness) {
        return Color.HSBtoRGB((System.currentTimeMillis() % (int) (seconds * 1000)) / (seconds * 1000), saturation, brightness);
    }

    public static int getColor(int offset) {
        return getAstolfo(10000000, offset, 0.5F);
    }

    public static int getColor(Color color) {
        return getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static int getColor(int brightness, int alpha) {
        return getColor(brightness, brightness, brightness, alpha);
    }

    public static int getColor(int red, int green, int blue) {
        return getColor(red, green, blue, 255);
    }

    public static int getColor(int red, int green, int blue, int alpha) {
        int color = clamp_int(alpha, 0, 255) << 24;

        color |= clamp_int(red, 0, 255) << 16;
        color |= clamp_int(green, 0, 255) << 8;
        color |= clamp_int(blue, 0, 255);

        return color;
    }

    public static Color getAstolfoColor(int delay, float offset) {
        float speed = 500;
        float hue = (float) (System.currentTimeMillis() % delay) + offset;
        while (hue > speed) hue -= speed;

        hue /= speed;

        if (hue > 0.5D) hue = 0.5F - hue - 0.5F;

        hue += 0.5F;

        return Color.getHSBColor(hue, 0.5F, 1.0F);
    }

    public static Color getColorWave(Color color, float offset) {
        float speed = 500;
        float hue = (float) (System.currentTimeMillis() % 10000000L) + offset;
        while (hue > speed) hue -= speed;

        hue /= speed;

        if (hue > 0.5D) hue = 0.5F - hue - 0.5F;

        hue += 0.5F;

        float[] colors = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(colors[0], 1.0F, hue);
    }

}
