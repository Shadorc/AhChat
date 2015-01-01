package me.shadorc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import me.shadorc.server.Server.Type;

class Client implements Runnable {

	private Server serv;
	private Socket s_chat;
	//	private Socket s_data;
	private PrintWriter out;
	private BufferedReader in;

	private String pseudo = "Unknown";

	Client(Socket s_chat, /* Socket s_data, */Server serv) {

		this.serv = serv;
		this.s_chat = s_chat;
		//		this.s_data = s_data;

		try {
			out = new PrintWriter(s_chat.getOutputStream());
			in = new BufferedReader(new InputStreamReader(s_chat.getInputStream()));

			pseudo = in.readLine();

			//If pseudo already exists, add number while pseudo exists (ex: Shadorc, Shadorc(1), Shadorc(2), ...)
			for(int i = 1; serv.getClients().containsKey((pseudo)); i++) {
				pseudo = pseudo + "(" + i + ")";
			}

			serv.addClient(out, pseudo);

			new Thread(this).start();

		} catch (IOException e) {
			ServerFrame.dispError("Erreur lors de la création du client : " + e.toString());
			this.quit();
		}
	}

	//	public void sendFile(File file) {
	//		send("[INFO] Envoie d'un fichier de " + file.length()/1024 + "ko.");
	//		try {
	//			new Thread(new Transfer(new FileInputStream(file), s_data.getOutputStream())).start();
	//		} catch (IOException e) {
	//			Frame.dispError("Erreur lors de l'envoit du fichier : " + e.toString() + ", annulation.");
	//			send("Erreur lors de l'envoit du fichier : " + e.toString() + ", annulation.");
	//			e.printStackTrace();
	//		}
	//	}

	@Override
	public void run() {

		String message;

		try {
			this.send("<b><font color=18B00A>* * Bienvenue ! Pour de l'aide entrer /help.");

			serv.sendAll(pseudo + " vient de se connecter.", Type.INFO);

			//Send the list of all connected people
			for(String name : serv.getClients().keySet()) {
				this.send("/connexion " + name);
			}

			//Attente en boucle des messages provenant du client (bloquant sur _in.read())
			while((message = in.readLine()) != null) {
				if(message.startsWith("/rename")) {
					this.rename(message);
				} else if(message.startsWith("/")) {
					this.send(Command.user(message));
				} else {
					// &lt; : "<" et &gt; : ">"
					serv.sendAll("<b><font color=blue>&lt;" + pseudo + "&gt;</b> " + message, Type.MESSAGE);
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
			serv.renClient(oldPseudo, pseudo);
		}
	}

	private void quit() {
		try {
			serv.sendAll(pseudo + " s'est déconnecté.", Type.INFO);
			serv.delClient(pseudo);
			s_chat.close();
			//s_data.close();
			out.flush();
			out.close();
			in.close();
		} catch (IOException e) {
			ServerFrame.dispError("Erreur lors de la fermeture du client : " + e.toString());
		}
	}
}