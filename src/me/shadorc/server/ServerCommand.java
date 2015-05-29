package me.shadorc.server;


public class ServerCommand {

	public static String user(ServerClient client, String command) {
		switch(command.toLowerCase().split(" ")[0]) {
			case "/rename":
				if(command.split(" ").length != 2)	return "<font color=red>Pseudo invalide.";
				client.setName(command.split(" ")[1]);
				return "Renommé en \"" + command.split(" ")[1] + "\".";
			case "/help":
				return "<u>Commandes disponibles :</u>\n" 
				+ ".....Changer de pseudo : /rename &lt;pseudo&gt;\n";
			default :
				return "Cette commande n'est pas supportée.\n" + ServerCommand.user(client, "/help");
		}
	}
}