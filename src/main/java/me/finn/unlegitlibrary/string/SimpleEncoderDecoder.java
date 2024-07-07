/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.string;

import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class SimpleEncoderDecoder extends DefaultMethodsOverrider {

    private static SecretKeySpec secretKey;
    private static byte[] keyByte;

    private static void setKey(final EncoderDecoderKeys key) throws NoSuchAlgorithmException {
        keyByte = key.getKey().getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        keyByte = sha.digest(keyByte);
        keyByte = Arrays.copyOf(keyByte, 16);
        secretKey = new SecretKeySpec(keyByte, "AES");
    }

    public static String encrypt(final Object toEncrypt, final EncoderDecoderKeys key) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        setKey(key);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return Base64.getEncoder().encodeToString(cipher.doFinal(toEncrypt.toString().getBytes(StandardCharsets.UTF_8)));
    }

    public static String decrypt(final Object toDecrypt, final EncoderDecoderKeys key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        setKey(key);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return new String(cipher.doFinal(Base64.getDecoder().decode(toDecrypt.toString())));
    }

    public static class EncoderDecoderKeys {
        public static final EncoderDecoderKeys BIT_KEY_128 = new EncoderDecoderKeys("Bar12345Bar12345");
        public static final EncoderDecoderKeys DEC1632DDCL542 = new EncoderDecoderKeys("Dec1632DDCL542");
        public static final EncoderDecoderKeys SSSHHHHHHHHHHH = new EncoderDecoderKeys("ssshhhhhhhhhhh!!!!");

        private final String key;

        public EncoderDecoderKeys(String key) {
            this.key = key;
        }

        public final String getKey() {
            return key;
        }
    }
}