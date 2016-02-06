package me.shadorc.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerUtility {

	public static String getIp() {
		String ip = "Unknown";
		BufferedReader in = null;

		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			ip = in.readLine();

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			try {
				if(in != null) in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ip;
	}

	public static String getTime() {
		return new SimpleDateFormat("HH:mm:ss ").format(new Date());
	}

	public static String toReadableUnit(long bytes) {
		int unit = 1000;
		if (bytes < unit) return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		char pre = ("kMGTPE").charAt(exp-1);
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
}
