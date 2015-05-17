package me.shadorc.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import me.shadorc.server.Server.Type;

public class Client implements Runnable {

	private Socket s_chat;
	private Socket s_data;

	private PrintWriter out;
	private BufferedReader in;

	private String pseudo;

	public Client(Socket s_chat, Socket s_data) {

		this.s_chat = s_chat;
		this.s_data = s_data;

		this.pseudo = "Unknown";

		try {
			out = new PrintWriter(s_chat.getOutputStream());
			in = new BufferedReader(new InputStreamReader(s_chat.getInputStream()));

			pseudo = in.readLine();

			//If pseudo already exists, add number while pseudo exists (ex: Shadorc, Shadorc(1), Shadorc(2), ...)
			for(int i = 1; Server.getClients().containsKey((pseudo)); i++) {
				pseudo = pseudo + "(" + i + ")";
			}

			Server.addClient(out, pseudo);

			new Thread(this).start();

		} catch (IOException e) {
			ServerFrame.dispError("Erreur lors de la création du client : " + e.getMessage());
			this.quit();
		}
	}

	public void sendFile(File file) {
		send("[INFO] Envoie d'un fichier de " + file.length()/1024 + "ko.");
		try {
			InputStream in = new FileInputStream(file);
			OutputStream out = s_data.getOutputStream();
			new Transfer(in, out).start();

		} catch (IOException e) {
			ServerFrame.dispError("Erreur lors de l'envoit du fichier : " + e.getMessage() + ", annulation.");
			send("Erreur lors de l'envoit du fichier : " + e.getMessage() + ", annulation.");
			e.printStackTrace();
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

			//Waiting loop messages from the client (blocking on _in.read ())
			while((message = in.readLine()) != null) {
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

	private void send(String message) {
		out.println(message);
		out.flush();
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
			out.flush();
			out.close();
			in.close();
		} catch (IOException e) {
			ServerFrame.dispError("Erreur lors de la fermeture du client : " + e.getMessage());
		}
	}
}