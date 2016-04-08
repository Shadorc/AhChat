package me.shadorc.client.frame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class Storage {

	private static Properties prop = new Properties();
	private static File conf = new File("config.properties");

	public enum Data {
		PSEUDO, IP, ICON;
	}

	public static void init() throws IOException {
		conf.createNewFile();
	}

	public static void store(Data data, Object value) {
		OutputStream output = null;

		try {
			output = new FileOutputStream(conf);

			prop.setProperty(data.toString(), value.toString());
			prop.store(output, null);

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			try {
				if (output != null) output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getData(Data data) {
		InputStream input = null;

		try {
			input = new FileInputStream(conf);
			prop.load(input);

			return prop.getProperty(data.toString());

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			try {
				if (input != null) input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}
}