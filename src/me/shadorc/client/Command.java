package me.shadorc.client;

import java.io.File;
import java.io.FileNotFoundException;

import me.shadorc.client.frame.ConnectedPanel;


public class Command {

	public static void serverCommand(String command) {
		switch(command.split(" ")[0]) {
			case "/connexion":
				ConnectedPanel.addUser(command.split(" ")[1]);
				break;
			case "/deconnexion":
				ConnectedPanel.removeUser(command.split(" ")[1]);
				break;
			case "/rename":
				ConnectedPanel.replaceUser(command.split(" ")[1], command.split(" ")[2]);
				break;
		}
	}

	public static void userCommand(String command) {
		switch(command.toLowerCase().split(" ")[0]) {
			case "/quit":
				Client.exit();
				return;
			case "/send":
				try {
					if(command.split(" ").length != 2) throw new FileNotFoundException();
					Client.sendFile(new File(command.split(" ")[1]));
				} catch (FileNotFoundException e) {
					ConnectedPanel.dispError(e, "Merci d'entrer un chemin de fichier valide.");
				}
				return;
			default:
				Client.sendMessage(command);
		}
	}
}
