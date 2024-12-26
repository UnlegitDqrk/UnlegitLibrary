/*
 * Copyright (C) 2024 UnlegitDqrk - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/UnlegitDqrk
 * See LICENSE-File if exists
 */

package me.finn.unlegitlibrary.file;

import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils extends DefaultMethodsOverrider {

    public static String getSuffix(File file) {
        String[] splitName = file.getName().split("\\.");
        return splitName[splitName.length - 1];
    }

    public static String readFileFromResource(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(filePath);
        if (inputStream == null) throw new FileNotFoundException("Can not load resource: " + filePath);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) content.append(line);
        }

        inputStream.close();
        return content.toString();
    }

    public static void deleteDirectoryRecursion(File file) {
        if (file.exists() && file.isDirectory()) {
            File[] entries = file.listFiles();
            if (entries != null) for (File entry : entries) deleteDirectoryRecursion(entry);
        }

        if (!file.exists()) return;
        file.delete();
    }

    public static String getName(File file) {
        String[] splitName = file.getName().split("\\.");
        return splitName[splitName.length - 2];
    }

    @Deprecated
    public static void copyResourceToFile(String resourceName, File targetFile, Class resourceClass) throws IOException {
        InputStream inputStream = resourceClass.getResourceAsStream("/" + resourceName);
        OutputStream outputStream = new FileOutputStream(targetFile);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) outputStream.write(buffer, 0, bytesRead);
    }

    public static void unzip(File source, String outputDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(source));
        ZipEntry entry = zis.getNextEntry();

        while (entry != null) {
            File file = new File(outputDirectory, entry.getName());

            if (entry.isDirectory()) file.mkdirs();
            else {
                File parent = file.getParentFile();
                if (!parent.exists()) parent.mkdirs();

                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
                    int bufferSize = Math.toIntExact(entry.getSize());
                    byte[] buffer = new byte[bufferSize > 0 ? bufferSize : 1];
                    int location;

                    while ((location = zis.read(buffer)) != -1) bos.write(buffer, 0, location);
                }
            }

            entry = zis.getNextEntry();
        }
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

    public static boolean isEmpty(File file) throws IOException {
        if (file.isDirectory()) {
            try (Stream<Path> entries = Files.list(file.toPath())) {
                return !entries.findFirst().isPresent();
            }
        } else if (file.isFile()) return file.length() == 0;

        return false;
    }

    public static String readFileFull(File file) throws IOException {
        Long length = file.length();
        byte[] content = new byte[length.intValue()];

        FileInputStream inputStream = new FileInputStream(file);
        inputStream.read(content);
        inputStream.close();

        return new String(content, StandardCharsets.UTF_8);
    }

    public static List<String> readFileLines(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
        String str;
        while ((str = in.readLine()) != null) lines.add(str);
        return lines;
    }

    public static void writeFile(File file, String text) throws IOException {
        Writer writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8));
        writer.write(text);
        writer.close();
    }

    public static void hideFile(File file) throws IOException {
        if (file.isHidden()) return;
        Files.setAttribute(Paths.get(file.getPath()), "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
    }

    public static void unHideFile(File file) throws IOException {
        if (!file.isHidden()) return;
        Files.setAttribute(Paths.get(file.getPath()), "dos:hidden", false, LinkOption.NOFOLLOW_LINKS);
    }
}