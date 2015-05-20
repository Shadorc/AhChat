package me.shadorc.client;

import java.io.PrintWriter;

public class Emission {

	private PrintWriter outChat;

	public Emission(PrintWriter out) {
		this.outChat = out;
	}

	public void sendMessage(String m) {
		outChat.println(m);
		outChat.flush();
	}
}