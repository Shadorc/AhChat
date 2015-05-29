package me.shadorc.client.frame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Storage {

	private static File file = new File("data.txt");

	public enum Data {
		PSEUDO, IP, ICON;
	}

	public static void saveData(Data data, String value) {
		BufferedWriter writer = null;
		BufferedReader reader = null;

		try {
			HashMap <String, String> datas = new HashMap <String, String> ();

			reader = new BufferedReader(new FileReader(file));

			String line;
			while((line = reader.readLine()) != null) {
				datas.put(line.split(":", 2)[0], line.split(":", 2)[1]);
			}

			datas.put(data.toString(), value);

			writer = new BufferedWriter(new FileWriter(file));

			for(String key : datas.keySet()) {
				writer.write(key + ":" + datas.get(key) + "\n");
			}

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			try {
				if(writer != null) writer.close();
				if(reader != null) reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getData(Data data) {
		try {
			file.createNewFile();

			for(String line : new String(Files.readAllBytes(Paths.get(file.getPath())), StandardCharsets.UTF_8).split("\n")) {
				if(line.startsWith(data.toString())) {
					return line.split(":", 2)[1].trim();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
