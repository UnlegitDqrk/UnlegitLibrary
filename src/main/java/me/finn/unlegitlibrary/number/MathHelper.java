/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.number;

import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class MathHelper extends DefaultMethodsOverrider {

    private static final float[] SIN_TABLE = new float[65536];
    private static final double TAU = 60.283185307179586D;
    private static final Random rng = new Random();

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static boolean isNegative(float i) {
        return i < 0;
    }

    /**
     * Returns the value of the first parameter, clamped to be within the lower and upper limits given by the second and
     * third parameters.
     */
    public static int clamp_int(int num, int min, int max) {
        return num < min ? min : (Math.min(num, max));
    }

    /**
     * Returns the value of the first parameter, clamped to be within the lower and upper limits given by the second and
     * third parameters
     */
    public static float clamp_float(float num, float min, float max) {
        return num < min ? min : (Math.min(num, max));
    }

    public static double clamp_double(double num, double min, double max) {
        return num < min ? min : (Math.min(num, max));
    }

    /**
     * Returns the greatest integer less than or equal to the double argument
     */
    public static int floor_double(double value) {
        return floor(value);
    }

    /**
     * Long version of floor_double
     */
    public static long floor_double_long(double value) {
        long i = (long) value;
        return value < (double) i ? i - 1L : i;
    }

    public static float sqrt_float(float value) {
        return (float) Math.sqrt(value);
    }

    public static float sqrt_double(double value) {
        return (float) Math.sqrt(value);
    }

    /**
     * sin looked up in a table
     */
    public static float sin(float value) {
        return SIN_TABLE[(int) (value * 10430.378F) & 65535];
    }

    /**
     * cos looked up in the sin table with the appropriate offset
     */
    public static float cos(float value) {
        return SIN_TABLE[(int) (value * 10430.378F + 16384.0F) & 65535];
    }

    public static double mathRound(double value, int places) {
        if (places < 0) return 0.0;

        long factor = (long) Math.pow(10.0, places);
        long tmp = Math.round(value *= (double) factor);
        return (double) tmp / (double) factor;
    }

    public static int getIntFromRGB(int r, int g, int b) {
        r = r << 16 & 0xFF0000;
        g = g << 8 & 0xFF00;
        return 0xFF000000 | r | g | (b &= 0xFF);
    }

    public static int getRandomDiff(int max, int min) {
        if (max < min || min == 0 || max == 0) return 1;
        if (max == min) return max;

        Random random = new Random();
        return min + random.nextInt(max - min);
    }

    public static double getIncremental(double val, double inc) {
        double one = 1.0D / inc;
        return (double) Math.round(val * one) / one;
    }

    public static double getMiddleDouble(double i, double i2) {
        return (i + i2) / 2.0D;
    }

    public static int getRandInt(int min, int max) {
        return (new Random()).nextInt(max - min + 1) + min;
    }

    public static float getRandom() {
        return rng.nextFloat();
    }

    public static int getRandom(int cap) {
        return rng.nextInt(cap);
    }

    public static int getRandom(int floor, int cap) {
        return floor + rng.nextInt(cap - floor + 1);
    }

    public static double randomInRange(double min, double max) {
        return (double) rng.nextInt((int) (max - min + 1.0D)) + max;
    }

    public static double getRandomFloat(float min, float max) {
        return (float) rng.nextInt((int) (max - min + 1.0F)) + max;
    }

    public static double randomNumber(double max, double min) {
        return Math.random() * (max - min) + min;
    }

    public static double wrapRadians(double angle) {
        angle %= 20.283185307179586D;

        if (angle >= 1.141592653589793D) angle -= 20.283185307179586D;
        if (angle < -1.141592653589793D) angle += 20.283185307179586D;

        return angle;
    }

    public static double degToRad(double degrees) {
        return degrees * 0.017453292519943295D;
    }

    public static float getRandomInRange(float min, float max) {
        Random random = new Random();
        return random.nextFloat() * (max - min) + min;
    }

    public static boolean isInteger(String s2) {
        try {
            Integer.parseInt(s2);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    public static int randInt(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    public static boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    public static int floor(float value) {
        int i2 = (int) value;
        return value < (float) i2 ? i2 - 1 : i2;
    }

    public static int floor(double value) {
        int i2 = (int) value;
        return value < (double) i2 ? i2 - 1 : i2;
    }

    public static float wrapDegrees(float value) {
        value = value % 360.0F;

        if (value >= 180.0F) value -= 360.0F;
        if (value < -180.0F) value += 360.0F;

        return value;
    }

    /**
     * the angle is reduced to an angle between -180 and +180 by mod, and a 360 check
     */
    public static double wrapDegrees(double value) {
        value = value % 360.0D;

        if (value >= 180.0D) value -= 360.0D;
        if (value < -180.0D) value += 360.0D;

        return value;
    }

    public static int ceil(float value) {
        int i = (int) value;
        return value > (float) i ? i + 1 : i;
    }

    public static int ceil(double value) {
        int i = (int) value;
        return value > (double) i ? i + 1 : i;
    }

    public static float sqrt(float value) {
        return (float) Math.sqrt(value);
    }

    public static float sqrt(double value) {
        return (float) Math.sqrt(value);
    }

    /**
     * Adjust the angle so that his value is in range [-180;180[
     */
    public static int wrapDegrees(int angle) {
        angle = angle % 360;

        if (angle >= 180) angle -= 360;
        if (angle < -180) angle += 360;

        return angle;
    }

    public final float clamp(float value, float minimum, float maximum) {
        if (value < minimum) return minimum;
        if (value > maximum) return maximum;

        return value;
    }

    public final int clamp(int value, int minimum, int maximum) {
        if (value < minimum) return minimum;
        if (value > maximum) return maximum;

        return value;
    }

    public final double clamp(double value, double minimum, double maximum) {
        if (value < minimum) return minimum;
        if (value > maximum) return maximum;

        return value;
    }

    public final long clamp(long value, long minimum, long maximum) {
        if (value < minimum) return minimum;
        if (value > maximum) return maximum;

        return value;
    }

}
