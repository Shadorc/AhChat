package me.shadorc.client;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.shadorc.client.frame.ConnectedPanel;

public class Transfer implements Runnable {

	private InputStream in;
	private OutputStream out;
	private File file;

	public Transfer(InputStream in, OutputStream out, File file) {
		this.in = in;
		this.out = out;
		this.file = file;
	}

	public void start() {
		new Thread(this).start();
	}

	@Override
	public void run() {

		byte buff[] = new byte[1024];
		int data;

		DataOutputStream dataOut = null;

		ConnectedPanel.dispMessage("[INFO] Client : Envoi en cours...");

		try {
			dataOut = new DataOutputStream(out);
			dataOut.writeLong(file.length());
			dataOut.flush();

			while((data = in.read(buff)) != -1) {
				out.write(buff, 0, data);
				out.flush();
			}

			ConnectedPanel.dispMessage("[INFO] Client : Fichier envoyé !");

		} catch (IOException e) {
			ConnectedPanel.dispError(e, "Erreur lors de l'envoi du fichier, " + e.getMessage() + ".");

			//		} finally {
			//			if(dataOut != null) {
			//				try {
			//					dataOut.close();
			//					System.out.println(this.getClass() + ": dataOut closed.");
			//				} catch (IOException e) {
			//					ConnectedPanel.dispError(e, "Erreur lors de la fermeture de l'envoi du fichier, " + e.getMessage() + ".");
			//				}
			//			}
		}
	}
}