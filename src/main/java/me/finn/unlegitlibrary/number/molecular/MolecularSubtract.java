/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.number.molecular;

import me.finn.unlegitlibrary.number.MathHelper;

public class MolecularSubtract {


    /**
     * @param k is the start number
     * @param n is the end number
     **/
    public static int useFormula(int k, int n) {
        if (!MathHelper.isNegative(n)) n = -(n);
        return ((-n - k + 1) * (-n + k) / 2) + k;
    }
}