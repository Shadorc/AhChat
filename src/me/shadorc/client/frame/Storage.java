package me.shadorc.client.frame;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Storage {

	private static File file = new File("data.txt");

	public enum Data {
		PSEUDO, IP, ICON;
	}

	public static void saveData(Data data, String value) {
		PrintWriter writer = null;
		try {
			file.createNewFile();

			writer = new PrintWriter(new FileWriter(file, true));
			writer.println(data.toString() + ":" + value);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writer != null) writer.close();
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
