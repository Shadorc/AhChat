package me.shadorc.client.frame;

import me.shadorc.client.Client;
import me.shadorc.client.Main;
import me.shadorc.server.ServerMain;

import javax.swing.*;
import java.awt.*;

public class Tray {

    private static TrayIcon icon;

    public static void init() {

        PopupMenu menu = new PopupMenu();

        MenuItem showServerItem = new MenuItem("Afficher le serveur");
        showServerItem.addActionListener(e -> {
            if (ServerMain.isOpen()) {
                ServerMain.getFrame().toFront();
                ServerMain.getFrame().setVisible(true);
            }
        });
        menu.add(showServerItem);

        MenuItem exitServerItem = new MenuItem("Fermer le serveur");
        exitServerItem.addActionListener(e -> {
            if (ServerMain.isOpen()) {
                ServerMain.exit();
            }
        });
        menu.add(exitServerItem);

        menu.addSeparator();

        MenuItem exitItem = new MenuItem("Quitter");
        exitItem.addActionListener(e -> Client.exit(true));
        menu.add(exitItem);

        icon = new TrayIcon(new ImageIcon(Tray.class.getResource("/res/icon.png")).getImage(), "AhChat", menu);
        icon.setImageAutoSize(true);

        try {
            SystemTray.getSystemTray().add(icon);
        } catch (AWTException e) {
            Main.showErrorDialog(e, "Erreur lors de la création du TrayIcon : " + e.getMessage());
        }
    }

    public static void alert() {
        if (!Main.getFrame().isFocused()) {
            icon.displayMessage(null, "[CHAT] Nouveau message", TrayIcon.MessageType.INFO);
        }
    }
}
