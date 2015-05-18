package me.shadorc.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

import javax.swing.filechooser.FileSystemView;

import me.shadorc.client.frame.ClientFrame;
import me.shadorc.client.frame.ConnectedPanel;

public class Reception implements Runnable {

	private BufferedReader inChat, inData;

	public Reception(BufferedReader inChat, BufferedReader inData) {
		this.inChat = inChat;
		this.inData = inData;
	}

	public void start() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		String message;

		try {

			this.waitingForFile();

			while((message = inChat.readLine()) != null) {
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

	//Le client envoie un fichier, on l'envoie à tous les autres clients
	private void waitingForFile() {
		//Ce thread attend en boucle la réception de données
		new Thread(new Runnable() {
			@Override
			public void run() {
				int bit;
				OutputStream out;

				try {
					if((bit = inData.read()) != -1) {
						//Client's Desktop with file's name
						File file = new File(FileSystemView.getFileSystemView().getHomeDirectory() + "\\" + "test.jpg");

						out = new FileOutputStream(file);

						ConnectedPanel.dispMessage("[INFO] Fichier en cours de réception.");

						while(bit != -1) {
							out.write(bit);
							out.flush();
							bit = inData.read();
						}
					}

				} catch (IOException e) {
					ConnectedPanel.dispError("Erreur lors de la réception du fichier, " + e.getMessage() + ", annulation.");

				} finally {
					ConnectedPanel.dispError("[INFO] Fichier reçu.");
					try {
						inData.close();
					} catch (IOException e) {
						ConnectedPanel.dispError("Erreur lors de la fin du transfert des données : " + e.getMessage());
					}
				}
			}
		}).start();
	}

	public void close() {
		try {
			inChat.close();
		} catch (IOException e) {
			ClientFrame.showError(e, "Erreur lors de la fermeture : " + e.getMessage());
		}
	}
}