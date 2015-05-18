package me.shadorc.server;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

public class Command {

	public static void admin(String command) {

		switch(command.toLowerCase()) {
			case "/quit":
				System.exit(0);
				return;
			case "/total":
				ServerFrame.dispMessage("Nombre de connectés : " + Server.getClients().size());
				return;
			case "/ip":
				ServerFrame.dispMessage("IP : " + Server.getIp());
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(Server.getIp()), null);
				return;
			case "/help":
				ServerFrame.dispMessage("Quitter : /quit");
				ServerFrame.dispMessage("Nombre de connectés : /total");
				ServerFrame.dispMessage("IP du serveur : /ip");
				return;
			default:
				ServerFrame.dispMessage("Cette commande n'est pas supportée.");
				Command.admin("/help");
		}
	}

	public static String user(Client client, String command) {

		switch(command.toLowerCase().split(" ")[0]) {
			case "/total":
				return "Nombre de connectés : " + Server.getClients().size();
			case "/rename":
				if(command.split(" ").length != 2)	return "<font color=red>Pseudo invalide.";
				client.setName(command.split(" ")[1]);
				return "Renommé en \"" + command.split(" ")[1] + "\".";
			case "/help":
				return "<u>Commandes disponibles :</u>\n" 
				+ ".....Nombre de connectés : /total\n" 
				+ ".....Changer de pseudo : /rename <pseudo>\n" 
				+ ".....Envoyer un fichier : /send <chemin>\n" 
				+ ".....Quitter : /quit";
			default :
				return "Cette commande n'est pas supportée.\n" + Command.user(client, "/help");
		}
	}
}