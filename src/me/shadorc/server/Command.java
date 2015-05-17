package me.shadorc.server;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import me.shadorc.server.Server.Type;

public class Command {

	public static void admin(String command) {

		switch(command.toLowerCase()) {
			case "/quit":
				System.exit(0);
				break;
			case "/total":
				ServerFrame.dispMessage("Nombre de connectés : " + Server.getClients().size());
				break;
			case "/ip":
				ServerFrame.dispMessage("IP : " + Server.getIp());
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(Server.getIp()), null);
				break;
			case "/help":
				ServerFrame.dispMessage("Quitter : /quit");
				ServerFrame.dispMessage("Nombre de connectés : /total");
				ServerFrame.dispMessage("IP du serveur : /ip");
				break;
		}

		if(command.startsWith("/")) {
			ServerFrame.dispMessage("Cette commande n'est pas supportée.");
			Command.admin("/help");

		} else {
			Server.sendAll("<b><font color='black'>[SERVER] : </b>" + command, Type.MESSAGE);
		}
	}

	public static String user(String command) {
		switch(command.toLowerCase()) {
			case "/total":
				return "Nombre de connectés : " + Server.getClients().size();
			case "/help":
				return "<u>Commandes disponibles :</u>\n" 
				+ ".....Nombre de connectés : /total\n" 
				+ ".....Changer de pseudo : /rename <pseudo>\n" 
				+ ".....Quitter : /quit";
			default :
				return "Cette commande n'est pas supportée.\n" + Command.user("/help");
		}
	}
}