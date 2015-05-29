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
		try {
			this.waitingForFile();

			String message;
			while((message = inChat.readLine()) != null) {
				if(message.startsWith("/")) {
					Command.serverCommand(message);
				} else {
					ConnectedPanel.dispMessage(message);
				}
			}

		} catch (IOException e) {
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
				OutputStream fileWriter = null;
				String fileName = null;

				try {
					//Send file's informations
					DataInputStream dataIn = new DataInputStream(inData);
					String[] infos = dataIn.readUTF().split("&");

					fileName = infos[0];
					long size = Long.parseLong(infos[1]);

					ConnectedPanel.addProgressBar("Téléchargement : " + fileName);

					File desktop = FileSystemView.getFileSystemView().getHomeDirectory();
					String name, format;
					if(fileName.contains(".")) {
						name = fileName.substring(0, fileName.lastIndexOf("."));
						format = fileName.substring(fileName.lastIndexOf("."));
					} else {
						name = fileName;
						format = "";
					}

					//While the file exists, change name
					File file = new File(desktop + "/" + name + format);
					for(int i = 1; file.exists(); i++) {
						file = new File(desktop + "/" + name + " (" + i + ")" + format);
					}

					fileWriter = new FileOutputStream(file);

					byte buff[] = new byte[1024];
					long total = 0;
					int data;

					while(total < size && (data = inData.read(buff)) > 0) {
						fileWriter.write(buff, 0, data);
						fileWriter.flush();
						total += data;
						ConnectedPanel.updateBar("Téléchargement : " + fileName, (int) (total * 100 / size));
					}

				} catch (SocketException ignore) {
					//Server's ending, ignore it.

				} catch (IOException e) {
					ConnectedPanel.dispError(e, "Erreur lors de la réception du fichier, " + e.getMessage());

				} finally {
					try {
						ConnectedPanel.removeProgressBar("Téléchargement : " + fileName);
						if(fileWriter != null) fileWriter.close();
					} catch (IOException e) {
						ConnectedPanel.dispError(e, "Erreur lors de la fermeture de la réception du fichier, " + e.getMessage());
					}
				}

				Reception.this.waitingForFile();
			}
		}).start();
	}
}