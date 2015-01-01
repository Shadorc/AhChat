package me.shadorc.server;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import me.shadorc.server.Server.Type;

class Command {

	private static Server serv;

	Command(Server serv) {
		Command.serv = serv;
	}

	protected static void admin(String command) {

		if(command.equalsIgnoreCase("/quit")) {
			System.exit(0);

		} else if(command.equalsIgnoreCase("/total")) {
			ServerFrame.dispMessage("Nombre de connectés : " + serv.getClients().size());

		} else if(command.equalsIgnoreCase("/ip")) {
			ServerFrame.dispMessage("IP : " + Server.ip);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(Server.ip), null);

		} else if(command.equals("/help")) {
			ServerFrame.dispMessage("Quitter : /quit");
			ServerFrame.dispMessage("Nombre de connectés : /total");
			ServerFrame.dispMessage("IP du serveur : /ip");

		} else if(command.startsWith("/")) {
			ServerFrame.dispMessage("Cette commande n'est pas supportée.");
			Command.admin("/help");

		} else {
			serv.sendAll("<b><font color='black'>[SERVER] : </b>" + command, Type.MESSAGE);
		}
	}

	protected static String user(String command) {

		if(command.equalsIgnoreCase("/total")) {
			return "Nombre de connectés : " + serv.getClients().size();

		} else if(command.equalsIgnoreCase("/help")) {
			return "<u>Commandes disponibles :</u>\n" 
					+ ".....Nombre de connectés : /total\n" 
					+ ".....Changer de pseudo : /rename <pseudo>\n" 
					+ ".....Quitter : /quit";

		} else {
			return "Cette commande n'est pas supportée.\n" + Command.user("/help");
		}
	}
}