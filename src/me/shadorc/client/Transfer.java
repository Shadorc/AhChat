package me.shadorc.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.shadorc.client.frame.ConnectedPanel;

public class Transfer implements Runnable {

	private InputStream in;
	private OutputStream out;

	public Transfer(InputStream in, OutputStream out, File file) {
		this.in = in;
		this.out = out;
	}

	public void start() {
		new Thread(this).start();
	}

	@Override
	public void run() {

		byte buff[] = new byte[1024];
		int data;

		ConnectedPanel.dispMessage("[INFO] Envoi en cours.");

		try {
			while((data = in.read(buff)) != -1) {
				out.write(buff, 0, data);
				out.flush();
			}

		} catch (IOException e) {
			ConnectedPanel.dispError("Erreur lors de l'envoi du fichier, " + e.getMessage() + ", annulation.");

		} finally {
			ConnectedPanel.dispMessage("[INFO] Fichier envoyé.");
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				ConnectedPanel.dispError("Erreur lors de la fin du transfert des données : " + e.getMessage());
			}
		}
	}
}