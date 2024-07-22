/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.number.vector;

import me.finn.unlegitlibrary.number.Quaternion;

public class Vector3 {
    public float x;
    public float y;
    public float z;

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector2 vector2) {
        this(vector2.x, vector2.y, 0);
    }

    public final Vector3 set(Vector2 vector2) {
        return set(vector2.x, vector2.y, 0);
    }

    public final float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public final float dot(Vector3 vector3) {
        return x * vector3.x + y * vector3.y + z * vector3.z;
    }

    public final Vector3 normalize() {
        float length = length();

        x /= length;
        y /= length;
        z /= length;

        return this;
    }

    public final Vector3 cross(Vector3 vector3) {
        float x_ = y * vector3.z - z * vector3.y;
        float y_ = z * vector3.x - x * vector3.z;
        float z_ = x * vector3.y - y * vector3.x;

        return new Vector3(x_, y_, z_);
    }

    public final Vector3 rotate(Vector3 axis, float angle) {
        float sinAngle = (float) Math.sin(-angle);
        float cosAngle = (float) Math.cos(-angle);

        return this.cross(axis.multiply(sinAngle)).                      // Rotation on local X
                add(multiply(cosAngle)).                                 // Rotation on local Z
                add(axis.multiply(dot(axis.multiply(1 - cosAngle)))); // Rotation on local Y
    }

    public final Vector3 rotate(Quaternion rotation) {
        Quaternion conjugate = rotation.conjugate();
        Quaternion w = rotation.multiply(this).multiply(conjugate);
        return new Vector3(w.x, w.y, w.z);
    }

    public final Vector3 lerp(Vector3 vector3, float lerpFactor) {
        return vector3.subtract(this).multiply(lerpFactor).add(this);
    }

    public final Vector3 add(Vector3 vector3) {
        return new Vector3(x + vector3.x, y + vector3.y, z + vector3.z);
    }

    public final Vector3 add(float f) {
        return new Vector3(x + f, y + f, z + f);
    }

    public final Vector3 subtract(Vector3 vector3) {
        return new Vector3(x - vector3.x, y - vector3.y, z - vector3.z);
    }

    public final Vector3 subtract(float f) {
        return new Vector3(x - f, y - f, z - f);
    }

    public Vector3 abs() {
        return new Vector3(Math.abs(x), Math.abs(y), Math.abs(z));
    }

    public final Vector3 multiply(Vector3 vector3) {
        return new Vector3(x * vector3.x, y * vector3.y, z * vector3.z);
    }

    public final Vector3 multiply(float f) {
        return new Vector3(x * f, y * f, z * f);
    }

    public final Vector3 divide(Vector3 vector3) {
        return new Vector3(x / vector3.x, y / vector3.y, z / vector3.z);
    }

    public final Vector3 divide(float f) {
        return new Vector3(x / f, y / f, z / f);
    }

    public final Vector3 set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;

        return this;
    }

    public final Vector3 set(Vector3 vector3) {
        return set(vector3.x, vector3.y, vector3.z);
    }

    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof Vector3)) return false;
        Vector3 vector3 = (Vector3) obj;
        return vector3.x == x && vector3.y == y && vector3.z == z;
    }

    @Override
    protected final Vector3 clone() {
        return new Vector3(x, y, z);
    }

    @Override
    public final String toString() {
        return "(" + x + " " + y + " " + z + ")";
    }
}