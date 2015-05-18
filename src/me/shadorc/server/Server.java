package me.shadorc.server;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Server implements Runnable {

	private static ArrayList <Client> clients;
	private static String ip;

	private ServerSocket ss_chat, ss_data;

	public void start() {
		new Thread(this).start();
	}

	public void stop() {
		try {
			ss_chat.close();
			ss_data.close();
		} catch (IOException e) {
			ServerFrame.showError(e, "Erreur lors de la fermeture du serveur : " + e.getMessage());
		}
	}

	@Override
	public void run() {

		ss_chat = null; //Chat Socket Server
		ss_data = null; //Data Socket Server

		clients = new ArrayList <Client> ();

		try {
			ss_chat = new ServerSocket(15000);
			ss_data = new ServerSocket(15001);

			try {
				ip = new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream())).readLine();
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(ip), null);
			} catch (IOException e) {
				ip = "Unknown";
			}

			ServerFrame.dispMessage("Welcome");
			ServerFrame.split();
			ServerFrame.dispMessage("Ports : " + ss_chat.getLocalPort() + " (chat) & " + ss_data.getLocalPort() + " (data)");
			ServerFrame.dispMessage("IP : " + ip);
			ServerFrame.split();
			Command.admin("/help");
			ServerFrame.split();

			while(true) {
				//Pending connection loop (blocking on accept())
				new Client(ss_chat.accept(), ss_data.accept());
			}

		} catch(BindException e) {
			ServerFrame.dispError("Un serveur est déjà lancé.");

		} catch(IOException e) {
			ServerFrame.dispError("Erreur lors de l'ouverture du serveur : " + e.getMessage());

		} finally {
			try {
				if(ss_chat != null && ss_data != null) {
					ss_chat.close();
					ss_data.close();
				}
			} catch(IOException e) {
				ServerFrame.dispError("Erreur lors de la fermeture du serveur : " + e.getMessage());
			}
		}

		System.err.println("Le serveur a été fermé avec succés.");
	}

	public enum Type {
		MESSAGE, COMMAND, INFO;
	}

	public static synchronized void sendAll(String message, Type type) {

		if(type == Type.MESSAGE || type == Type.INFO) {
			message = new SimpleDateFormat("HH:mm:ss ").format(new Date()) 
					+ (type == Type.INFO ? "<b><i><font color=red>[INFO]</b></i> " : "") 
					+ message;
			ServerFrame.dispMessage(message);
		}

		for(Client client : clients) {
			client.sendMessage(message);
		}
	}

	public static synchronized void sendAll(ArrayList <Integer> data) {
		for(Client client : clients) {
			client.sendData(data);
		}
	}

	public static synchronized void addClient(Client client) {
		clients.add(client);
		sendAll("/connexion " + client.getName(), Type.COMMAND);
	}

	public static synchronized void delClient(Client client) {
		clients.remove(client);
		sendAll("/deconnexion " + client.getName(), Type.COMMAND);
	}

	public static ArrayList <Client> getClients() {
		return clients;
	}

	public static String getIp() {
		return ip;
	}
}