package me.shadorc.client.frame;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import me.shadorc.client.Client;
import me.shadorc.client.Main;
import me.shadorc.server.ServerMain;

public class Tray {

	private static TrayIcon icon;

	public static void initialize() {

		PopupMenu menu = new PopupMenu();

		MenuItem quitItem = new MenuItem("Quitter");
		quitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Client.exit(true);
			}
		});
		menu.add(quitItem);

		MenuItem closeServerItem = new MenuItem("Fermer le serveur");
		closeServerItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ServerMain.exit();
			}
		});
		menu.add(closeServerItem);

		MenuItem openServerItem = new MenuItem("Ouvrir le serveur");
		openServerItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO
			}
		});
		menu.add(openServerItem);

		icon = new TrayIcon(new ImageIcon(Tray.class.getResource("/res/icon.png")).getImage(), "AhChat", menu);
		icon.setImageAutoSize(true);

		try {
			SystemTray.getSystemTray().add(icon);
		} catch (AWTException e) {
			Main.showErrorDialog(e, "Erreur lors de la création du TrayIcon : " + e.getMessage());
		}
	}

	public static void alert() {
		if(!Main.getFrame().isFocused()) {
			icon.displayMessage(null, "[CHAT] Nouveau message", TrayIcon.MessageType.INFO);
		}
	}
}
