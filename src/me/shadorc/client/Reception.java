package me.shadorc.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import javax.swing.filechooser.FileSystemView;

import me.shadorc.client.frame.ConnectedPanel;

public class Reception implements Runnable {

	private BufferedReader inChat;
	private InputStream inData;

	public Reception(BufferedReader inChat, InputStream inData) {
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
			ConnectedPanel.dispError(e, "Le serveur a été fermé.");

		} catch (IOException e) {
			ConnectedPanel.dispError(e, "Erreur lors de la récéption des messages : " + e.getMessage());

		} finally {
			Client.exit();
		}
	}

	//Le client envoie un fichier, on l'envoie à tous les autres clients
	private void waitingForFile() {
		//Ce thread attend en boucle la réception de données
		new Thread(new Runnable() {
			@Override
			public void run() {
				byte buff[] = new byte[1024];
				int data;

				OutputStream out = null;
				DataInputStream dataIn = null;

				try {
					dataIn = new DataInputStream(inData);
					long size = dataIn.readLong();
					String fileName = dataIn.readUTF();

					//Client's Desktop with file's name
					File file = new File(FileSystemView.getFileSystemView().getHomeDirectory() + "\\" + fileName);
					for(int i = 0; file.exists(); i++) {
						file = new File(FileSystemView.getFileSystemView().getHomeDirectory() + "\\" + fileName + "(" + i + ")");
					}
					out = new FileOutputStream(file);

					ConnectedPanel.dispMessage("[INFO] Client : Fichier en cours de réception.");

					long total = 0;
					while(total < size && (data = inData.read(buff, 0, size-total > buff.length ? buff.length : (int)(size-total))) > 0) {
						out.write(buff, 0, data);
						out.flush();
						total += data;
					}

					ConnectedPanel.dispMessage("[INFO] Fichier reçu et enregistré.");

				} catch (IOException e) {
					ConnectedPanel.dispError(e, "Erreur lors de la réception du fichier, " + e.getMessage() + ".");

				} finally {
					if(out != null/* && dataIn != null*/) {
						try {
							out.close();
							//							dataIn.close();
							System.out.println(this.getClass() + ": out closed.");
						} catch (IOException e) {
							ConnectedPanel.dispError(e, "Erreur lors de la fermeture de la réception du fichier, " + e.getMessage() + ".");
						}
					}
				}
			}
		}).start();
	}
}