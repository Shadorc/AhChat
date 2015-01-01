package me.shadorc.client;

import java.io.PrintWriter;

public class Emission {

	private static PrintWriter out;

	protected Emission(PrintWriter out) {
		Emission.out = out;
	}

	public static void sendMessage(String m) {
		out.println(m);
		out.flush();
	}

	protected void close() {
		out.flush();
		out.close();
	}
}