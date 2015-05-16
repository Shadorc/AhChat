package me.shadorc.client.frame;

import java.util.Arrays;

import javax.swing.JTextArea;

public class Command {

	private static JTextArea users;

	protected Command(JTextArea users) {
		Command.users = users;
	}

	public static void execute(String command) {
		switch(command.split(" ")[0]) {
			case "/connexion":
				Command.addUser(command.split(" ")[1]);
				break;
			case "/deconnexion":
				Command.removeUser(command.split(" ")[1]);
				break;
			case "/rename":
				Command.replaceUser(command.split(" ")[1], command.split(" ")[2]);
				break;
		}
	}

	private static void addUser(String user) {
		if(!Arrays.asList(users.getText().split("\n")).contains(user)) {
			users.append(user + "\n");
		}
	}

	private static void removeUser(String user) {
		users.setText(users.getText().replace(user, ""));
	}

	private static void replaceUser(String oldName, String newName) {
		users.setText(users.getText().replace(oldName, newName));
	}
}
