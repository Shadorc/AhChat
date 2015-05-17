package me.shadorc.client;

import me.shadorc.client.frame.ConnectedPanel;


public class Command {

	public static void execute(String command) {
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
}
