package me.shadorc.server;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import me.shadorc.utility.ServerUtility;

public class Server implements Runnable {

	private static ArrayList <ServerClient> clients;

	private ServerSocket ss_chat, ss_data;
	private String ip;

	public void start() {
		new Thread(this).start();
	}

	public void stop() {
		try {
			Server.sendAll("/serverClosed", MessageType.COMMAND);
			if(ss_chat != null) ss_chat.close();
			if(ss_data != null) ss_data.close();
		} catch (IOException e) {
			ServerFrame.showError(e, "Erreur lors de la fermeture du serveur : " + e.getMessage());
			e.printStackTrace();
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

			ip = ServerUtility.getIp();
			ip = Base64.encode(ip.getBytes());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(ip), null);

			ServerFrame.updateInfos(ip, ss_chat.getLocalPort(), ss_data.getLocalPort());

			ServerFrame.dispMessage("Welcome");
			ServerFrame.dispMessage("-------------------------------------------");

			while(true) {
				//Pending connection loop (blocking on accept())
				new ServerClient(ss_chat.accept(), ss_data.accept());
			}

		} catch(SocketException ignore) {
			//Server's ending, ignore it

		} catch(IOException e) {
			ServerFrame.dispError(e, "Erreur lors de l'ouverture du serveur : " + e.getMessage());

		} finally {
			try {
				if(ss_chat != null) ss_chat.close();
				if(ss_data != null) ss_data.close();
			} catch(IOException e) {
				ServerFrame.dispError(e, "Erreur lors de la fermeture du serveur : " + e.getMessage());
			}
		}
	}

	public enum MessageType {
		NORMAL, COMMAND, INFO;
	}

	public static synchronized void sendAll(String message, MessageType type) {

		if(type != MessageType.COMMAND) {
			message = ServerUtility.getTime()
					+ (type == MessageType.INFO ? "<b><i><font color=red>[INFO]</b></i> " : "") 
					+ message;
			ServerFrame.dispMessage(message);
		}

		for(ServerClient client : clients) {
			client.sendMessage(message);
		}
	}

	public static synchronized void addClient(ServerClient client) {
		clients.add(client);
		ServerFrame.addUser(client.getName());
		Server.sendAll("/connexion " + client.getName(), MessageType.COMMAND);
	}

	public static synchronized void delClient(ServerClient client) {
		clients.remove(client);
		ServerFrame.removeUser(client.getName());
		Server.sendAll("/deconnexion " + client.getName(), MessageType.COMMAND);
	}

	public static ArrayList <ServerClient> getClients() {
		return clients;
	}
}