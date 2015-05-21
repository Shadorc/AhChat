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

	private static ArrayList <ServerClient> clients;
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

		clients = new ArrayList <ServerClient> ();

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
			ServerCommand.admin("/help");
			ServerFrame.split();

			while(true) {
				//Pending connection loop (blocking on accept())
				new ServerClient(ss_chat.accept(), ss_data.accept());
			}

		} catch(BindException e) {
			ServerFrame.dispError(e, "Un serveur est déjà lancé.");

		} catch(IOException e) {
			ServerFrame.dispError(e, "Erreur lors de l'ouverture du serveur : " + e.getMessage());

		} finally {
			try {
				if(ss_chat != null && ss_data != null) {
					ss_chat.close();
					ss_data.close();
				}
			} catch(IOException e) {
				ServerFrame.dispError(e, "Erreur lors de la fermeture du serveur : " + e.getMessage());
			}
		}
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

		for(ServerClient client : clients) {
			client.sendMessage(message);
		}
	}

	public static synchronized void addClient(ServerClient client) {
		clients.add(client);
		sendAll("/connexion " + client.getName(), Type.COMMAND);
	}

	public static synchronized void delClient(ServerClient client) {
		clients.remove(client);
		sendAll("/deconnexion " + client.getName(), Type.COMMAND);
	}

	public static ArrayList <ServerClient> getClients() {
		return clients;
	}

	public static String getIp() {
		return ip;
	}
}