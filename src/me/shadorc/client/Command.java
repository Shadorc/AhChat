package me.shadorc.client;

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
		if(command.equalsIgnoreCase("/quit")) {
			Client.exit();

		} else if(command.startsWith("/send")) {
			try {
				if(command.split(" ").length != 2) throw new FileNotFoundException("Chemin du fichier non spécifié.");
				Client.sendFile(command.split(" ")[1]);
			} catch (FileNotFoundException e1) {
				ConnectedPanel.dispError("Merci d'entrer le chemin du fichier à envoyer.");
			}
		}
	}
}
