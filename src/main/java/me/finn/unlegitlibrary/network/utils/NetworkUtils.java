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
import java.net.ServerSocket;
import java.net.URL;

public class NetworkUtils extends DefaultMethodsOverrider {

    public static int findFreePort() {
        ServerSocket socket = null;

        try {
            socket = new ServerSocket(0);
            socket.setReuseAddress(true);

            int port = socket.getLocalPort();

            try {
                socket.close();
            } catch (IOException ignored) {
            }

            return port;
        } catch (IOException ignored) {
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }

        throw new IllegalStateException("Could not find a free TCP/IP port to start embedded Jetty HTTP Server on");
    }

    public static void downloadFile(String link, File outputFile) throws IOException {
        URL url = new URL(link);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(outputFile);

        byte[] buffer = new byte[1024];
        int count = 0;

        while ((count = bis.read(buffer, 0, 1024)) != -1) fis.write(buffer, 0, count);

        fis.close();
        bis.close();
    }

    public static String getPublicIPAddress() throws IOException {
        String ipServiceURL = "https://api.ipify.org?format=text";
        URL url = new URL(ipServiceURL);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return reader.readLine();
        }
    }

}
