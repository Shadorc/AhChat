package me.shadorc.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.filechooser.FileSystemView;

import me.shadorc.client.Transfer;
import me.shadorc.client.frame.ConnectedPanel;
import me.shadorc.server.Server.Type;

public class Client implements Runnable {

	private Socket s_chat;
	private Socket s_data;

	private PrintWriter outChat;
	private BufferedReader inChat;

	private PrintWriter outData;
	private BufferedReader inData;

	private String pseudo;

	public Client(Socket s_chat, Socket s_data) {

		this.s_chat = s_chat;
		this.s_data = s_data;

		this.pseudo = "Unknown";

		try {
			outChat = new PrintWriter(s_chat.getOutputStream());
			inChat = new BufferedReader(new InputStreamReader(s_chat.getInputStream()));

			outData = new PrintWriter(s_data.getOutputStream());
			inData = new BufferedReader(new InputStreamReader(s_data.getInputStream()));

			pseudo = inChat.readLine();

			//If pseudo already exists, add number while pseudo exists (ex: Shadorc, Shadorc(1), Shadorc(2), ...)
			for(int i = 1; Server.getClients().containsKey((pseudo)); i++) {
				pseudo = pseudo + "(" + i + ")";
			}

			Server.addClient(outChat, pseudo);

			new Thread(this).start();

		} catch (IOException e) {
			ServerFrame.dispError("Erreur lors de la création du client : " + e.getMessage());
			this.quit();
		}
	}

	@Override
	public void run() {
		String message;

		try {
			this.send("<b><font color=18B00A>* * Bienvenue ! Pour de l'aide entrer /help.");

			Server.sendAll(pseudo + " vient de se connecter.", Type.INFO);

			//Send the list of all connected people
			for(String name : Server.getClients().keySet()) {
				this.send("/connexion " + name);
			}

			//Ce thread attend en boucle la réception de données
			new Thread(new Runnable() {
				@Override
				public void run() {
					InputStream in = null;
					OutputStream out = null;

					byte buff[] = new byte[1024];
					int data;

					try {
						in = s_data.getInputStream();

						if((data = in.read(buff)) != -1) {
							//Client's Desktop with file's name
							File file = new File(FileSystemView.getFileSystemView().getHomeDirectory() + "\\" + "test.jpg");

							out = new FileOutputStream(file);

							ConnectedPanel.dispMessage("[INFO] Fichier en cours de réception.");

							//					timer.start();

							while(data != -1) {
								out.write(buff, 0, data);
								out.flush();
								data = in.read(buff);
							}
						}

					} catch (IOException e) {
						ConnectedPanel.dispError("Erreur lors de la réception du fichier, " + e.getMessage() + ", annulation.");

					} finally {
						ConnectedPanel.dispError("[INFO] Fichier reçu.");
						try {
							//							timer.stop();
							in.close();
							out.close();
							out.flush();
						} catch (IOException | NullPointerException e) {
							ConnectedPanel.dispError("Erreur lors de la fin du transfert des données : " + e.getMessage());
						}
					}
				}
			}).start();

			//Waiting loop messages from the client (blocking on _in.read ())
			while((message = inChat.readLine()) != null) {
				if(message.startsWith("/rename")) {
					this.rename(message);
				} else if(message.startsWith("/")) {
					this.send(Command.user(message));
				} else {
					// &lt; : "<" et &gt; : ">"
					Server.sendAll("<b><font color=blue>&lt;" + pseudo + "&gt;</b> " + message, Type.MESSAGE);
				}
			}

		} catch (IOException ignored) {
			//Client leave, the exception doesn't need to be managed

		} finally {
			this.quit();
		}
	}

	public void sendFile(File file) {
		send("[INFO] Envoi d'un fichier de " + file.length()/1024 + "ko.");
		try {
			InputStream in = new FileInputStream(file);
			OutputStream out = s_data.getOutputStream();
			new Transfer(in, out, file).start();

		} catch (IOException e) {
			ServerFrame.dispError("Erreur lors de l'envoi du fichier : " + e.getMessage() + ", annulation.");
			send("Erreur lors de l'envoi du fichier : " + e.getMessage() + ", annulation.");
			e.printStackTrace();
		}
	}

	private void send(String message) {
		outChat.println(message);
		outChat.flush();
	}

	private void rename(String message) {
		if(message.split(" ").length != 2) {
			this.send("<font color=red>Pseudo invalide.");

		} else {
			String oldPseudo = pseudo;
			pseudo = message.split(" ")[1];
			Server.renClient(oldPseudo, pseudo);
		}
	}

	private void quit() {
		try {
			Server.sendAll(pseudo + " s'est déconnecté.", Type.INFO);
			Server.delClient(pseudo);
			s_chat.close();
			//s_data.close();
			outChat.flush();
			outChat.close();
			inChat.close();
		} catch (IOException e) {
			ServerFrame.dispError("Erreur lors de la fermeture du client : " + e.getMessage());
		}
	}
}