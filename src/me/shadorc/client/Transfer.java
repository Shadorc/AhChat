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

		ConnectedPanel.dispMessage("[INFO] Envoi en cours...");

		try {
			while((data = in.read(buff)) != -1) {
				out.write(buff, 0, data);
				out.flush();
			}

			ConnectedPanel.dispMessage("[INFO] Fichier envoyé !");
		} catch (IOException e) {
			ConnectedPanel.dispError(e, "Erreur lors de l'envoi du fichier, " + e.getMessage() + ", annulation.");
		}

		System.out.println("Finish : " + new Object(){}.getClass());
	}
}