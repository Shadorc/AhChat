package me.shadorc.client.frame;

import javax.swing.JTextArea;

public class Command {

	private static JTextArea users;

	protected Command(JTextArea users) {
		Command.users = users;
	}

	public static void execute(String command) {
		if(command.startsWith("/connexion")) {
			Command.addUser(command.split(" ")[1]);
		} else if(command.startsWith("/deconnexion")) {
			Command.removeUser(command.split(" ")[1]);
		} else if(command.startsWith("/rename")) {
			Command.replaceUser(command.split(" ")[1], command.split(" ")[2]);
		}
	}

	private static void addUser(String user) {
		for(String name : users.getText().split("\n")) {
			if(name.equals(user)) {
				return;
			}
		}

		users.append(user + "\n");
	}

	private static void removeUser(String user) {
		users.setText(users.getText().replace(user, ""));
	}

	private static void replaceUser(String oldName, String newName) {
		users.setText(users.getText().replace(oldName, newName));
	}
}
