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

		} catch (IOException ignore) {
			ConnectedPanel.dispError("Le serveur a été fermé.");

		} finally {
			Client.exit(false);
		}
	}

	//This thread is waiting for receiving data
	private void waitingForFile() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				OutputStream out = null;
				DataInputStream dataIn = null;

				try {
					//Send file's informations
					dataIn = new DataInputStream(inData);
					long size = dataIn.readLong();
					String fileName = dataIn.readUTF();

					ConnectedPanel.dispMessage("[INFO] Client : Fichier en cours de réception.");

					File desktop = FileSystemView.getFileSystemView().getHomeDirectory();
					String name = fileName.substring(0, fileName.lastIndexOf("."));
					String format = fileName.substring(fileName.lastIndexOf("."));

					//While the file exists, change name
					File file = new File(desktop + "\\" + name + format);
					for(int i = 1; file.exists(); i++) {
						file = new File(desktop + "\\" + name + " (" + i + ")" + format);
					}

					out = new FileOutputStream(file);

					byte buff[] = new byte[1024];
					long total = 0;
					int data;

					while(total < size && (data = inData.read(buff)) != -1) {
						out.write(buff, 0, data);
						out.flush();
						total += data;
					}

					ConnectedPanel.dispMessage("[INFO] Fichier reçu et enregistré.");

				} catch (SocketException ignore) {
					//Server's ending, ignore it.

				} catch (IOException e) {
					ConnectedPanel.dispError(e, "Erreur lors de la réception du fichier, " + e.getMessage());

				} finally {
					if(out != null) {
						try {
							out.close();
						} catch (IOException e) {
							ConnectedPanel.dispError(e, "Erreur lors de la fermeture de la réception du fichier, " + e.getMessage());
						}
					}
				}
				
				Reception.this.waitingForFile();
			}
		}).start();
	}
}