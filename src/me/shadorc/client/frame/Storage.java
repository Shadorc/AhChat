package me.shadorc.client.frame;

import java.io.*;
import java.util.Properties;

public class Storage {

    private static final Properties PROPERTIES = new Properties();
    private static final File CONFIG_FILE = new File("config.properties");

    public enum Data {
        PSEUDO, IP, ICON;
    }

    public static void init() throws IOException {
        CONFIG_FILE.createNewFile();
    }

    public static void store(Data data, Object value) {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            PROPERTIES.setProperty(data.toString(), value.toString());
            PROPERTIES.store(output, null);

        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    public static String getData(Data data) {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            PROPERTIES.load(input);
            return PROPERTIES.getProperty(data.toString());

        } catch (IOException err) {
            err.printStackTrace();
        }

        return null;
    }
}