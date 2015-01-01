package me.shadorc.client.frame;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import me.shadorc.client.Client;

public class Tray {

	private static TrayIcon icon;
	private static JFrame frame;

	protected Tray(JFrame frame) {

		Tray.frame = frame;
		PopupMenu menu = new PopupMenu();

		MenuItem closeItem = new MenuItem("Close");
		closeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Client.exit();
			}
		});
		menu.add(closeItem);

		icon = new TrayIcon(new ImageIcon(this.getClass().getResource("/res/icone.png")).getImage(), "Chat", menu);
		icon.setImageAutoSize(true);

		try {
			SystemTray.getSystemTray().add(icon);
		} catch (AWTException e) {
			ConnectedPanel.dispError("Erreur lors de la cr�ation du TrayIcon : " + e.toString());
		}
	}

	protected static void alerteMessage() {
		if(!frame.isFocused()) {
			icon.displayMessage(null, "[CHAT] Nouveau message", TrayIcon.MessageType.INFO);
		}
	}

	public static void close() {
		SystemTray.getSystemTray().remove(icon);
	}
}
