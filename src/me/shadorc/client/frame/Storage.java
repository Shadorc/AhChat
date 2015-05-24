package me.shadorc.client.frame;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Storage {

	private static File file = new File("./data.json");

	public enum Data {
		PSEUDO, ICON, IP;
	}

	public static String get(Data data) {
		try {
			if(!file.exists() || new String(Files.readAllBytes(Paths.get(file.getPath())), StandardCharsets.UTF_8).isEmpty()) {
				file.createNewFile();

			} else {
				JSONObject jsonObject = (JSONObject) new JSONParser().parse(new FileReader(file));
				return (String) jsonObject.get(data.toString());
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void save(Data data, String value) {
		FileWriter writer = null;

		try {
			String text = new String(Files.readAllBytes(Paths.get(file.getPath())), StandardCharsets.UTF_8);

			JSONObject jsonObject;
			if(text.length() != 0) {
				jsonObject = (JSONObject) new JSONParser().parse(text);
			} else {
				jsonObject = new JSONObject();
			}
			jsonObject.put(data.toString(), value);

			writer = new FileWriter(file);
			writer.write(jsonObject.toJSONString());

		} catch (IOException | ParseException e) {
			e.printStackTrace();

		} finally {
			try {
				if(writer != null) {
					writer.flush();
					writer.close();
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
