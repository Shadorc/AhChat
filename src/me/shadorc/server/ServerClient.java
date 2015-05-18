package me.shadorc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import me.shadorc.server.Server.Type;

public class ServerClient implements Runnable {

	private Socket s_chat;
	private Socket s_data;

	private PrintWriter outData;
	private PrintWriter outChat;
	private BufferedReader inChat;

	private String name;
	private String ip;

	public ServerClient(Socket s_chat, Socket s_data) {

		this.s_chat = s_chat;
		this.s_data = s_data;

		this.name = "Unknown";
		this.ip = s_chat.getRemoteSocketAddress().toString();

		try {
			outChat = new PrintWriter(s_chat.getOutputStream());
			inChat = new BufferedReader(new InputStreamReader(s_chat.getInputStream()));

			outData = new PrintWriter(s_data.getOutputStream());

			name = inChat.readLine();

			//			If pseudo already exists, add number while pseudo exists (ex: Shadorc, Shadorc(1), Shadorc(2), ...)
			//						for(int i = 1; Server.getClients().containsKey((name)); i++) {
			//							name = name + "(" + i + ")";
			//						}

			Server.addClient(this);

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
			this.sendMessage("<b><font color=18B00A>* * Bienvenue ! Pour de l'aide entrer /help.");

			Server.sendAll(name + " vient de se connecter.", Type.INFO);

			//Send the list of all connected people
			for(ServerClient client : Server.getClients()) {
				this.sendMessage("/connexion " + client.getName());
			}

			this.waitingForFile();

			//Waiting for messages from the client (blocking on inChat.readLine())
			while((message = inChat.readLine()) != null) {
				if(message.startsWith("/")) {
					this.sendMessage(ServerCommand.user(this, message));
				} else {
					// &lt; : "<" et &gt; : ">"
					Server.sendAll("<b><font color=blue>&lt;" + name + "&gt;</b> " + message, Type.MESSAGE);
				}
			}

		} catch (IOException ignored) {
			//Client leave, the exception doesn't need to be managed

		} finally {
			this.quit();
		}
	}

	public void sendMessage(String message) {
		outChat.println(message);
		outChat.flush();
	}

	public void sendData(ArrayList <Integer> data) {
		for(int bit : data) {
			outData.write(bit);
			outData.flush();
		}
	}

	//Le client envoie un fichier, on l'envoie à tous les autres clients
	private void waitingForFile() {
		//Ce thread attend en boucle la réception de données
		new Thread(new Runnable() {
			@Override
			public void run() {
				InputStream in = null;

				byte buff[] = new byte[1024];
				int bit; 

				ArrayList <Integer> data = new ArrayList <Integer> ();;

				try {
					in = s_data.getInputStream();

					while((bit = in.read(buff)) != -1) {
						data.add(bit);
					}

					Server.sendAll(data);

				} catch (IOException e) {
					ServerClient.this.sendMessage("Erreur lors de la réception du fichier, " + e.getMessage() + ", annulation.");

				} finally {
					ServerClient.this.sendMessage("[INFO] Fichier reçu.");
					try {
						in.close();
					} catch (IOException e) {
						ServerClient.this.sendMessage("Erreur lors de la fin du transfert des données : " + e.getMessage());
					}
				}
			}
		}).start();
	}

	public String getIp() {
		return ip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		Server.sendAll("/rename " + this.name + " " + name, Type.COMMAND);
		Server.sendAll(this.name + " s'est renommé en " + name + ".", Type.INFO);
		this.name = name;
	}

	private void quit() {
		try {
			Server.sendAll(name + " s'est déconnecté.", Type.INFO);
			Server.delClient(this);
			s_chat.close();
			s_data.close();
			outData.close();
			outChat.close();
			inChat.close();
		} catch (IOException e) {
			ServerFrame.dispError("Erreur lors de la fermeture du client : " + e.getMessage());
		}
	}
}