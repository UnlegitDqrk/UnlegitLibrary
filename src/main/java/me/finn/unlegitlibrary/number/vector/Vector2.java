/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.number.vector;

public class Vector2 {

    public float x;
    public float y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector3 vector3) {
        this(vector3.x, vector3.y);
    }

    public final Vector2 set(Vector3 vector3) {
        return set(vector3.x, vector3.y);
    }

    public final float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public final float dot(Vector2 vector2) {
        return x * vector2.x + y * vector2.y;
    }

    public final Vector2 normalize() {
        float length = length();

        x /= length;
        y /= length;

        return this;
    }

    public final Vector2 rotate(float angle) {
        double rad = Math.toRadians(angle);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        return new Vector2((float) (x * cos - y * sin), (float) (x * sin + y * cos));
    }

    public final Vector2 add(Vector2 vector2) {
        return new Vector2(x + vector2.x, y + vector2.y);
    }

    public final Vector2 add(float f) {
        return new Vector2(x + f, y + f);
    }

    public final Vector2 subtract(Vector2 vector2) {
        return new Vector2(x - vector2.x, y - vector2.y);
    }

    public final Vector2 subtract(float f) {
        return new Vector2(x - f, y - f);
    }

    public final Vector2 multiply(Vector2 vector2) {
        return new Vector2(x * vector2.x, y * vector2.y);
    }

    public final Vector2 multiply(float f) {
        return new Vector2(x * f, y * f);
    }

    public final Vector2 divide(Vector2 vector2) {
        return new Vector2(x / vector2.x, y / vector2.y);
    }

    public final Vector2 divide(float f) {
        return new Vector2(x / f, y / f);
    }

    public final Vector2 set(float x, float y) {
        this.x = x;
        this.y = y;

        return this;
    }

    public final Vector2 set(Vector2 vector2) {
        return set(vector2.x, vector2.y);
    }

    public final Vector2 lerp(Vector2 dest, float lerpFactor) {
        return dest.subtract(this).multiply(lerpFactor).add(this);
    }

    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof Vector2 vector2)) return false;
        return vector2.x == x && vector2.y == y;
    }

    @Override
    protected final Vector2 clone() {
        return new Vector2(x, y);
    }

    @Override
    public final String toString() {
        return "(" + x + " " + y + ")";
    }
}