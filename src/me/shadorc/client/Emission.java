package me.shadorc.client;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import me.shadorc.client.frame.ConnectedPanel;

public class Emission {

	private PrintWriter outChat;
	private OutputStream outData;

	public Emission(PrintWriter outChat, OutputStream outData) {
		this.outChat = outChat;
		this.outData = outData;
	}

	public void sendMessage(String m) {
		outChat.println(m);
		outChat.flush();
	}

	public void sendFile(File file) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				if(ConnectedPanel.getUsers().size() == 1) {
					ConnectedPanel.dispMessage("[INFO] Il n'y a personne à qui envoyer ce fichier.");
					return;
				}

				DataOutputStream dataOut = null;
				FileInputStream fileReader = null;

				try {
					ConnectedPanel.dispMessage("[INFO] Client : Envoi de " + file.getName() + " en cours...");

					fileReader = new FileInputStream(file);

					dataOut = new DataOutputStream(outData);
					dataOut.writeLong(file.length());
					dataOut.writeUTF(file.getName());
					dataOut.flush();

					byte buff[] = new byte[1024];
					int data;

					while((data = fileReader.read(buff)) > 0) {
						outData.write(buff, 0, data);
						outData.flush();
					}

					ConnectedPanel.dispMessage("[INFO] Client : " + file.getName() + " envoyé !");

				} catch (FileNotFoundException e) {
					ConnectedPanel.dispError(e, "Merci d'entrer un chemin de fichier valide.");

				} catch (IOException e) {
					ConnectedPanel.dispError(e, "Erreur lors de l'envoi du fichier, " + e.getMessage());

				} finally {
					try {
						if(fileReader != null) fileReader.close();
					} catch (IOException e) {
						ConnectedPanel.dispError(e, "Erreur lors de la fermeture de l'envoi du fichier, " + e.getMessage());
					}
				}
			}
		}).start();
	}
}