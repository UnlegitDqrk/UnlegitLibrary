package me.finn.unlegitlibrary.file;

import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigurationManager extends DefaultMethodsOverrider {
    private final Properties properties = new Properties();
    private final File configFile;

    public ConfigurationManager(File configFile) {
        this.configFile = configFile;
    }

    public final boolean isSet(String key) {
        return properties.containsKey(key);
    }

    public final void loadProperties() throws IOException {
        try (FileInputStream in = new FileInputStream(configFile)) {
            properties.load(in);
        }
    }

    public final void saveProperties() throws IOException {
        try (FileOutputStream out = new FileOutputStream(configFile)) {
            properties.store(out, null);
        }
    }

    public final long getLong(String key) {
        return Long.parseLong(properties.getProperty(key));
    }

    public final double getDouble(String key) {
        return Double.parseDouble(properties.getProperty(key));
    }

    public final float getFloat(String key) {
        return Float.parseFloat(properties.getProperty(key));
    }

    public final int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    public final Object getObject(String key) {
        return (Object) properties.getProperty(key);
    }

    public Map<String, String> getMap(String key) {
        String value = properties.getProperty(key);
        Map<String, String> map = new HashMap<>();

        if (value != null) {
            String[] entries = value.split(",");

            for (String entry : entries) {
                String[] pair = entry.split("=");
                if (pair.length == 2) map.put(pair[0], pair[1]);
            }
        }

        return map;
    }

    public final String getString(String key) {
        return properties.getProperty(key);
    }

    public final List<String> getList(String key) {
        String value = properties.getProperty(key);

        if (value != null) return Arrays.asList(value.split(","));
        else return new ArrayList<>();
    }

    public final void set(String key, long value) {
        properties.setProperty(key, String.valueOf(value));
    }

    public final void set(String key, int value) {
        properties.setProperty(key, String.valueOf(value));
    }

    public final void set(String key, double value) {
        properties.setProperty(key, String.valueOf(value));
    }

    public final void set(String key, float value) {
        properties.setProperty(key, String.valueOf(value));
    }

    public final void set(String key, Object value) {
        properties.setProperty(key, String.valueOf(value));
    }

    public final void set(String key, String value) {
        properties.setProperty(key, value);
    }

    public final void set(String key, Map<String, String> map) {
        String value = map.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(","));

        properties.setProperty(key, value);
    }

    public final void set(String key, List<String> list) {
        String value = list.stream().collect(Collectors.joining(","));
        properties.setProperty(key, value);
    }
}