/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.number;

import me.finn.unlegitlibrary.number.vector.Vector2;
import me.finn.unlegitlibrary.number.vector.Vector3;

public class Quaternion {

    public float x;
    public float y;
    public float z;
    public float w;

    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Quaternion(Vector3 vector3) {
        this(vector3.x, vector3.y, vector3.z, 0);
    }

    public Quaternion(Vector2 vector2) {
        this(new Vector3(vector2));
    }

    public final float length() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public final float dot(Quaternion quaternion) {
        return x * quaternion.x + y * quaternion.y + z * quaternion.z + w * quaternion.w;
    }

    public final Quaternion normalize() {
        float length = length();

        x /= length;
        y /= length;
        z /= length;
        w /= length;

        return this;
    }

    public final Quaternion conjugate() {
        return new Quaternion(-x, -y, -z, w);
    }

    public final Quaternion multiply(Quaternion quaternion) {
        float w_ = w * quaternion.w - x * quaternion.x - y * quaternion.y - z * quaternion.z;
        float x_ = x * quaternion.w + w * quaternion.x + y * quaternion.z - z * quaternion.y;
        float y_ = y * quaternion.w + w * quaternion.y + z * quaternion.x - x * quaternion.z;
        float z_ = z * quaternion.w + w * quaternion.z + x * quaternion.y - y * quaternion.x;

        return new Quaternion(x_, y_, z_, w_);
    }

    public final Quaternion multiply(Vector3 vector3) {
        float w_ = -x * vector3.x - y * vector3.y - z * vector3.z;
        float x_ = w * vector3.x + y * vector3.z - z * vector3.y;
        float y_ = w * vector3.y + z * vector3.x - x * vector3.z;
        float z_ = w * vector3.z + x * vector3.y - y * vector3.x;

        return new Quaternion(x_, y_, z_, w_);
    }

    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof Quaternion)) return false;
        Quaternion quaternion = (Quaternion) obj;
        return quaternion.x == x && quaternion.y == y && quaternion.z == z && quaternion.w == w;
    }

    @Override
    protected final Quaternion clone() {
        return new Quaternion(x, y, z, w);
    }

    @Override
    public final String toString() {
        return "(" + x + " " + y + " " + z + " " + w + ")";
    }

}