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
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils extends DefaultMethodsOverrider {

    public static String getSuffix(File file) {
        String[] splitName = file.getName().split("\\.");
        return splitName[splitName.length - 1];
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

    public static boolean isEmpty(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (Stream<Path> entries = Files.list(path)) {
                return !entries.findFirst().isPresent();
            }
        }

        return false;
    }

    public static String readFile(File file) throws IOException {
        Long length = file.length();
        byte[] content = new byte[length.intValue()];

        FileInputStream inputStream = new FileInputStream(file);
        inputStream.read(content);
        inputStream.close();

        return new String(content, StandardCharsets.UTF_8);
    }

    public static void copyFile(File sourceFile, File toFolder, boolean replaceExisting) throws IOException {
        // Check if the source file exists and is a regular file
        if (!sourceFile.exists() || !sourceFile.isFile()) return;

        // Check if the destination folder exists and is a directory
        if (!toFolder.exists() || !toFolder.isDirectory()) return;

        // Get the name of the source file
        String fileName = sourceFile.getName();
        File destinationFile = new File(toFolder, fileName);


        if (replaceExisting) Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        else Files.copy(sourceFile.toPath(), destinationFile.toPath());
    }

    public static void copyFiles(File fromFolder, File toFolder, boolean replaceExisting) throws IOException {
        // Check if the source directory exists and is a directory
        if (!fromFolder.exists() || !fromFolder.isDirectory()) return;

        // Check if the destination directory exists and is a directory
        if (!toFolder.exists() || !toFolder.isDirectory()) return;

        // List all files in the source directory
        File[] filesToCopy = fromFolder.listFiles();
        if (filesToCopy == null) return;

        // Iterate through the files and copy them to the destination directory
        for (File file : filesToCopy) {
            Path source = file.toPath();
            Path destination = new File(toFolder, file.getName()).toPath();

            if (replaceExisting) Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            else Files.copy(source, destination);
        }
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