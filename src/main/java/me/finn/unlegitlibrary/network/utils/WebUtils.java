/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.network.utils;

import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WebUtils extends DefaultMethodsOverrider {

    private static final String ACCEPTED_RESPONSE = "application/json";

    public static String httpGet(String url) throws Exception {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        if (connection.getResponseCode() == 204) return null;

        return InputStreamUtils.readInputStream(connection.getInputStream());
    }

    public static byte[] httpGetByte(String url) throws Exception {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        if (connection.getResponseCode() == 204) return null;

        return InputStreamUtils.readInputStream2Byte(connection.getInputStream());
    }

    public static String toHttps(String url) {
        return url.startsWith("http") ? "https" + url.substring(4) : url;
    }

    public static String toHttp(String url) {
        return url.startsWith("https") ? "http" + url.substring(5) : url;
    }

    public static void setBaseHeaders(HttpsURLConnection connection) {
        connection.setRequestProperty("Accept-encoding", "gzip");
        connection.setRequestProperty("Accept-Language", "en-US");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (XboxReplay; XboxLiveAuth/3.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
    }

    public static HttpURLConnection createURLConnection(String url) throws IOException {
        HttpURLConnection connection = null;

        connection = (HttpURLConnection) new URL(url).openConnection();

        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setRequestProperty("Accept-Language", "en-US");
        connection.setRequestProperty("Accept-Charset", "UTF-8");

        return connection;
    }

    public static String readResponse(HttpURLConnection connection) throws IOException {
        String redirection = connection.getHeaderField("Location");
        if (redirection != null) return readResponse(createURLConnection(redirection));

        StringBuilder response = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getResponseCode() >= 400 ? connection.getErrorStream() : connection.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) response.append(line).append('\n');

        return response.toString();
    }

    private static String readResponse(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        return sb.toString();
    }

}