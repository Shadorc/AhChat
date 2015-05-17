package me.shadorc.client;

import java.io.PrintWriter;

public class Emission {

	private PrintWriter out;

	Emission(PrintWriter out) {
		this.out = out;
	}

	public void sendMessage(String m) {
		out.println(m);
		out.flush();
	}

	public void close() {
		out.flush();
		out.close();
	}
}