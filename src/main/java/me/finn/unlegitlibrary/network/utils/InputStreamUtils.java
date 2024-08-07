/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.network.utils;

import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class InputStreamUtils extends DefaultMethodsOverrider {

    public static void writeInputStream(InputStream input, File file) {
        try {
            Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static byte[] readInputStream2Byte(InputStream inputStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int length;

        while ((length = inputStream.read(buffer)) != -1) outputStream.write(buffer, 0, length);

        outputStream.close();
        inputStream.close();

        return outputStream.toByteArray();
    }

    public static String readInputStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int length;

        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }

        byteArrayOutputStream.close();
        inputStream.close();

        return new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
    }

    public static void downloadFile(String urlStr, File outputFile) throws IOException {
        URL url = new URL(urlStr);

        BufferedInputStream bufferedInputStream = new BufferedInputStream(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);

        byte[] buffer = new byte[1024];
        int count = 0;

        while ((count = bufferedInputStream.read(buffer, 0, 1024)) != -1) fileOutputStream.write(buffer, 0, count);

        fileOutputStream.close();
        bufferedInputStream.close();
    }
}