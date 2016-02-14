package me.shadorc.server;


public class ServerCommand {

	public static String user(ServerClient client, String command) {

		String[] splitCmd = command.split(" ");
		String cmd = splitCmd[0].toLowerCase();
		String arg = (splitCmd.length > 1) ? command.split(" ", 2)[1] : null;

		switch(cmd) {
			case "/rename":
				if(arg == null)	return "<font color=red>Pseudo invalide.";
				client.setName(arg);
				return "Renommé en \"" + arg + "\".";

			case "/help":
				return "<u>Commandes disponibles :</u>\n" 
				+ ".....Changer de pseudo : /rename &lt;pseudo&gt;\n";

			default :
				return "Cette commande n'est pas supportée.\n" + ServerCommand.user(client, "/help");
		}
	}
}