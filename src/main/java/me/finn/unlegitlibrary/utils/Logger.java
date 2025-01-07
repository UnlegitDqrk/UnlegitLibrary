package me.finn.unlegitlibrary.utils;

import me.finn.unlegitlibrary.file.FileUtils;
import me.finn.unlegitlibrary.string.color.ConsoleColor;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.System;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

/**
 * Logger by shock9 Interactive
 */
public final class Logger {
    private File logFolder;
    private File latestLogFile;

    private boolean isInitialized = false;

    public Logger(File logFolder, boolean changeCharset, boolean addShutdownHook) throws IOException, NoSuchFieldException, IllegalAccessException {
        if (changeCharset) {
            try {
                Field field = Charset.class.getDeclaredField("defaultCharset");
                field.setAccessible(true);
                field.set(null, StandardCharsets.UTF_8);
            } catch (IllegalAccessException exception) {
                exception.printStackTrace();
            }
        }

        System.setProperty("client.encoding.override", "UTF-8");
        System.setProperty("file.encoding", "UTF-8");

        // Basic setup for log folder and latest log file
        this.logFolder = logFolder;
        latestLogFile = new File(logFolder, "log-latest.txt");

        logFolder.mkdir();

        if (latestLogFile.exists()) latestLogFile.delete();
        latestLogFile.createNewFile();

        if (addShutdownHook) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    shutdown();
                } catch (IOException exception) {
                    exception("Failed to shutdown logger", exception);
                }
            }));
        }

        isInitialized = true;
    }

    // Renaming latest log to current date and yeah
    public final void shutdown() throws IOException {
        if (!isInitialized) return;

        // Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss");
        Date date = new Date();

        String timeStamp = formatter.format(date);

        // Backup latest log file to current date and time
        File backupLogFile = new File(logFolder, "log-" + timeStamp + ".txt");
        backupLogFile.createNewFile();
        Files.copy(latestLogFile.toPath(), backupLogFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        FileUtils.writeFile(backupLogFile, FileUtils.readFileFull(latestLogFile));

        isInitialized = false;
    }

    private final void writeToLog(String log) throws IOException {
        if (isInitialized)
            FileUtils.writeFile(latestLogFile, FileUtils.readFileFull(latestLogFile) + System.lineSeparator() + log);
    }

    public final void log(String string) {
        // Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();

        String timeStamp = formatter.format(date);

        // Writing log
        String log = timeStamp + " [LOG] " + string;

        try {
            writeToLog(log);
        } catch (IOException ignored) {
        }
    }

    public final void info(String info) {
        // Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();

        String timeStamp = formatter.format(date);

        // Writing log
        String log = timeStamp + " [INFO] " + info;

        System.out.println(ConsoleColor.WHITE + log + ConsoleColor.RESET);

        try {
            writeToLog(log);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public final void warn(String warn) {
        // Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();

        String timeStamp = formatter.format(date);

        // Writing log
        String log = timeStamp + " [WARN] " + warn;

        System.out.println(ConsoleColor.YELLOW + log + ConsoleColor.RESET);

        try {
            writeToLog(log);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public final void error(String error) {
        // Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();

        String timeStamp = formatter.format(date);

        // Writing log
        String log = timeStamp + " [ERROR] " + error;

        System.out.println(ConsoleColor.RED + log + ConsoleColor.RESET);

        try {
            writeToLog(log);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public final void exception(String infoLine, Exception exception) {
        // Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();

        String timeStamp = formatter.format(date);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String stackTrace = sw.toString();

        // Writing log
        String log =
                timeStamp + " [EXCEPTION-INFO] " + infoLine + System.lineSeparator() +
                timeStamp + " [EXCEPTION-MESSAGE] " + exception.getMessage() + System.lineSeparator() +
                timeStamp + " [EXCEPTION-STACKTRACE] " + stackTrace;

        System.out.println(ConsoleColor.RED + log + ConsoleColor.RESET);

        try {
            writeToLog(log);
        } catch (IOException exception1) {
            exception1.printStackTrace();
        }
    }

    public final void debug(String debug) {
        // Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();

        String timeStamp = formatter.format(date);

        // Writing log
        String log = timeStamp + " [DEBUG] " + debug;

        System.out.println(ConsoleColor.BLUE + log + ConsoleColor.RESET);

        try {
            writeToLog(log);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}