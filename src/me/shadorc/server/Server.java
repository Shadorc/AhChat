package me.shadorc.server;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Server implements Runnable {

	private static HashMap <String, PrintWriter> clients;
	private static String ip;

	public void start() {
		new Thread(this).start();
	}

	@Override
	public void run() {

		ServerSocket ss_chat = null; //Chat Socket Server
		ServerSocket ss_data = null; //Data Socket Server

		clients = new HashMap <> ();

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
				//Pending connection loop (blocking on ss.accept)
				new Client(ss_chat.accept(), ss_data.accept());
			}

		} catch (IOException e) {
			ServerFrame.dispError("Erreur lors de l'ouverture du serveur : " + e.toString());

		} finally {
			try {
				ss_chat.close();
				ss_data.close();
			} catch (IOException | NullPointerException e) {
				ServerFrame.dispError("Erreur lors de la fermeture du serveur : " + e.toString());
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

		for(String client : clients.keySet()) {
			clients.get(client).println(message);
			clients.get(client).flush();
		}
	}

	public static synchronized void addClient(PrintWriter out, String pseudo) {
		clients.put(pseudo, out);
		sendAll("/connexion " + pseudo, Type.COMMAND);
	}

	public static synchronized void renClient(String oldName, String newName) {
		PrintWriter oldOut = clients.get(oldName);
		clients.remove(oldName);
		clients.put(newName, oldOut);

		sendAll(oldName + " s'est renommé en " + newName + ".", Type.INFO);
		sendAll("/rename " + oldName + " " + newName, Type.COMMAND);
	}

	public static synchronized void delClient(String pseudo) {
		clients.remove(pseudo);
		sendAll("/deconnexion " + pseudo, Type.COMMAND);
	}

	protected static HashMap <String, PrintWriter> getClients() {
		return clients;
	}

	public static String getIp() {
		return ip;
	}
}