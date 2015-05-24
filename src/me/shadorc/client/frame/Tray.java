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

	public static void initialize(JFrame _frame) {

		frame = _frame;
		PopupMenu menu = new PopupMenu();

		MenuItem closeItem = new MenuItem("Close");
		closeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Client.exit(true);
			}
		});
		menu.add(closeItem);

		icon = new TrayIcon(new ImageIcon(Tray.class.getResource("/res/icon.png")).getImage(), "AhChat", menu);
		icon.setImageAutoSize(true);

		try {
			SystemTray.getSystemTray().add(icon);
		} catch (AWTException e) {
			ConnectedPanel.dispError(e, "Erreur lors de la création du TrayIcon : " + e.getMessage());
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
