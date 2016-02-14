package me.shadorc.client;

import javax.swing.ImageIcon;

import me.shadorc.client.frame.ConnectedPanel;


public class Command {

	public static void serverCommand(String command) {

		switch(command.split(" ")[0]) {
			case "/connexion":
				ConnectedPanel.getUsersList().addUser(command.split(" ")[1], new ImageIcon(Command.class.getResource("/res/icon.png")));
				break;
			case "/deconnexion":
				ConnectedPanel.getUsersList().removeUser(command.split(" ")[1]);
				break;
			case "/rename":
				ConnectedPanel.getUsersList().replaceUser(command.split(" ")[1], command.split(" ")[2]);
				break;
			case "/serverClosed":
				Client.exit(false);
		}
	}
}
