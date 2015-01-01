package me.shadorc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;

import javax.swing.JOptionPane;

import me.shadorc.client.frame.Command;
import me.shadorc.client.frame.ConnectedPanel;

class Reception implements Runnable {

	private BufferedReader in;

	protected Reception(BufferedReader in) {
		this.in = in;
	}

	@Override
	public void run() {

		String message;

		try {
			while((message = in.readLine()) != null) {
				if(message.startsWith("/")) {
					Command.execute(message);
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

	protected void close() {
		try {
			in.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}
}