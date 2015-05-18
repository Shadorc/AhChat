package me.shadorc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;

import me.shadorc.client.frame.ClientFrame;
import me.shadorc.client.frame.ConnectedPanel;

public class Reception implements Runnable {

	private BufferedReader in;

	public Reception(BufferedReader in) {
		this.in = in;
	}

	public void start() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		String message;

		try {
			while((message = in.readLine()) != null) {
				if(message.startsWith("/")) {
					Command.serverCommand(message);
				} else {
					ConnectedPanel.dispMessage(message);
				}
			}

		} catch (SocketException e) {
			ConnectedPanel.dispError("Le serveur a été fermé.");

		} catch (IOException e) {
			ConnectedPanel.dispError("Erreur lors de la récéption des messages : " + e.toString());

		} finally {
			this.close();
		}
	}

	public void close() {
		try {
			in.close();
		} catch (IOException e) {
			ClientFrame.showError(e, "Erreur lors de la fermeture : " + e.getMessage());
		}
	}
}