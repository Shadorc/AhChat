package com.shadorc.ahchat.client.frame;

import com.shadorc.ahchat.client.Client;
import com.shadorc.ahchat.client.Main;
import com.shadorc.ahchat.server.ServerManager;

import javax.swing.ImageIcon;
import java.awt.*;

public class Tray {

    private static TrayIcon icon;

    public static void init() {

        final PopupMenu menu = new PopupMenu();

        final MenuItem showServerItem = new MenuItem("Afficher le serveur");
        showServerItem.addActionListener(e -> {
            if (ServerManager.getInstance().isStarted()) {
                ServerManager.getInstance().getFrame().toFront();
                ServerManager.getInstance().getFrame().setVisible(true);
            }
        });
        menu.add(showServerItem);

        final MenuItem exitServerItem = new MenuItem("Fermer le serveur");
        exitServerItem.addActionListener(e -> {
            if (ServerManager.getInstance().isStarted()) {
                ServerManager.getInstance().stop();
            }
        });
        menu.add(exitServerItem);

        menu.addSeparator();

        final MenuItem exitItem = new MenuItem("Quitter");
        exitItem.addActionListener(ignored -> {
            Client.getInstance().disconnect();
            System.exit(0);
        });
        menu.add(exitItem);

        Tray.icon = new TrayIcon(new ImageIcon(Tray.class.getResource("/icon.png")).getImage(), "AhChat", menu);
        Tray.icon.setImageAutoSize(true);

        try {
            SystemTray.getSystemTray().add(Tray.icon);
        } catch (final AWTException e) {
            Main.showErrorDialog(e, "Erreur lors de la création du TrayIcon : " + e.getMessage());
        }
    }

    public static void alert() {
        if (!Main.getFrame().isFocused()) {
            Tray.icon.displayMessage(null, "[CHAT] Nouveau message", TrayIcon.MessageType.INFO);
        }
    }
}
