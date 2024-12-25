package me.finn.unlegitlibrary.utils;

import me.finn.unlegitlibrary.number.MathHelper;

public class Color {

    public float red = 1;
    public float green = 1;
    public float blue = 1;
    public float alpha = 1;

    public Color(float red, float green, float blue, float alpha) {
        this.red = MathHelper.clamp_float(red, 0f, 1f);
        this.green = MathHelper.clamp_float(green, 0f, 1f);
        this.blue = MathHelper.clamp_float(blue, 0f, 1f);
        this.alpha = MathHelper.clamp_float(alpha, 0f, 1f);
    }

    public Color(float red, float green, float blue) {
        this(red, green, blue, 1);
    }

    public static final Color COLOR_BLACK = new Color(0, 0, 0);
    public static final Color COLOR_WHITE = new Color(1, 1, 1);
    public static final Color COLOR_RED = new Color(1, 0, 0);
    public static final Color COLOR_GREEN = new Color(0, 1, 0);
    public static final Color COLOR_BLUE = new Color(0, 0, 1);
    public static final Color COLOR_YELLOW = new Color(1, 1, 0);
    public static final Color COLOR_ORANGE = new Color(1, 0, 1);
    public static final Color COLOR_MAGENTA = new Color(1, 0, 1);
    public static final Color COLOR_CYAN = new Color(0, 1, 0);
    public static final Color COLOR_WINE = new Color(0.5f, 0.5f, 0.5f);
    public static final Color COLOR_FORREST = new Color(0, 0.5f, 0);
    public static final Color COLOR_MARINE = new Color(0, 0, 0.5f);

    @Override
    public String toString() {
        return "(" + red + "," + green + "," + blue + "," + alpha + ")";
    }

    @Override
    protected final Color clone() throws CloneNotSupportedException {
        return new Color(red, green, blue, alpha);
    }

    public final java.awt.Color toAwtColor() {
        return new java.awt.Color(red, green, blue, alpha);
    }

    public static Color fromAwtColor(java.awt.Color awtColor) {
        return new Color(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue(), awtColor.getAlpha());
    }

    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof Color)) return false;
        Color equalTo = (Color)obj;
        return equalTo.alpha == alpha && equalTo.red == red && equalTo.green == green && equalTo.blue == blue;
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }
}
