/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.number;

import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.nio.FloatBuffer;

public class Matrix4x4 extends DefaultMethodsOverrider {
    private float[][] matrix = new float[4][4];

    public Matrix4x4() {
        setIdentity();
    }

    public static Matrix4x4 orthographic(float left, float right, float bottom, float top, float near, float far) {
        Matrix4x4 matrix4x4 = new Matrix4x4();

        float width = right - left;
        float height = top - bottom;
        float depth = far - near;

        matrix4x4.matrix[0][0] = 2f / width;
        matrix4x4.matrix[1][1] = 2f / height;
        matrix4x4.matrix[2][2] = 2f / depth;

        matrix4x4.matrix[3][0] = -(right + left) / width;
        matrix4x4.matrix[3][1] = -(top + bottom) / height;
        matrix4x4.matrix[3][2] = -(far + near) / depth;

        return matrix4x4;
    }

    public final float[][] getMatrix() {
        return matrix;
    }

    public final void setMatrix(float[][] matrix) {
        this.matrix = matrix;
    }

    public final void setIdentity() {
        matrix[0][0] = 1;
        matrix[0][1] = 0;
        matrix[0][2] = 0;
        matrix[0][3] = 0;
        matrix[1][0] = 0;
        matrix[1][1] = 1;
        matrix[1][2] = 0;
        matrix[1][3] = 0;
        matrix[2][0] = 0;
        matrix[2][1] = 0;
        matrix[2][2] = 1;
        matrix[2][3] = 0;
        matrix[3][0] = 0;
        matrix[3][1] = 0;
        matrix[3][2] = 0;
        matrix[3][3] = 1;
    }

    public final void getBuffer(FloatBuffer buffer) {
        buffer.put(matrix[0][0]).put(matrix[0][1]).put(matrix[0][2]).put(matrix[0][3]);
        buffer.put(matrix[1][0]).put(matrix[1][1]).put(matrix[1][2]).put(matrix[1][3]);
        buffer.put(matrix[2][0]).put(matrix[2][1]).put(matrix[2][2]).put(matrix[2][3]);
        buffer.put(matrix[3][0]).put(matrix[3][1]).put(matrix[3][2]).put(matrix[3][3]);
        buffer.flip();
    }
}
