package com.shadorc.ahchat.client.frame;

import java.io.*;
import java.util.Properties;

public class Storage {

    private static final Properties PROPERTIES = new Properties();
    private static final File CONFIG_FILE = new File("config.properties");

    public enum Data {
        PSEUDO, IP, ICON;
    }

    public static void init() throws IOException {
        Storage.CONFIG_FILE.createNewFile();
    }

    public static void store(final Data data, final Object value) {
        try (final OutputStream output = new FileOutputStream(Storage.CONFIG_FILE)) {
            Storage.PROPERTIES.setProperty(data.toString(), value.toString());
            Storage.PROPERTIES.store(output, null);

        } catch (final IOException err) {
            err.printStackTrace();
        }
    }

    public static String getData(final Data data) {
        try (final InputStream input = new FileInputStream(Storage.CONFIG_FILE)) {
            Storage.PROPERTIES.load(input);
            return Storage.PROPERTIES.getProperty(data.toString());

        } catch (final IOException err) {
            err.printStackTrace();
        }

        return null;
    }
}