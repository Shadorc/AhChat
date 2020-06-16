package com.shadorc.ahchat.client.frame;

import java.io.*;
import java.util.Properties;

public class Storage {

    private static Storage instance;

    static {
        Storage.instance = new Storage();
    }

    private final Properties properties;
    private final File configFile;

    public enum Data {
        PSEUDO, IP, ICON;
    }

    private Storage() {
        this.properties = new Properties();
        this.configFile = new File("config.properties");
        try {
            this.configFile.createNewFile();
        } catch (final IOException err) {
            System.err.println("An error occurred while initializing storage file: " + err.getMessage());
            err.printStackTrace();
            System.exit(1);
        }
    }

    public void save(final Data data, final Object value) {
        try (final OutputStream output = new FileOutputStream(this.configFile)) {
            this.properties.setProperty(data.toString(), value.toString());
            this.properties.store(output, null);

        } catch (final IOException err) {
            System.err.println("An error occurred while saving data: " + err.getMessage());
            err.printStackTrace();
        }
    }

    public String find(final Data data) {
        try (final InputStream input = new FileInputStream(this.configFile)) {
            this.properties.load(input);
            return this.properties.getProperty(data.toString());

        } catch (final IOException err) {
            System.err.println("An error occurred while finding data: " + err.getMessage());
            err.printStackTrace();
        }

        return null;
    }

    public static Storage getInstance() {
        return Storage.instance;
    }
}