package com.shadorc.ahchat.client.frame;

import com.shadorc.ahchat.client.Client;
import com.shadorc.ahchat.client.Main;
import com.shadorc.ahchat.server.ServerManager;

import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Tray {

    private static TrayIcon icon;

    public static void init() {

        PopupMenu menu = new PopupMenu();

        MenuItem showServerItem = new MenuItem("Afficher le serveur");
        showServerItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ServerManager.isOpen()) {
                    ServerManager.getFrame().toFront();
                    ServerManager.getFrame().setVisible(true);
                }
            }
        });
        menu.add(showServerItem);

        MenuItem exitServerItem = new MenuItem("Fermer le serveur");
        exitServerItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ServerManager.isOpen()) {
                    ServerManager.stop();
                }
            }
        });
        menu.add(exitServerItem);

        menu.addSeparator();

        MenuItem exitItem = new MenuItem("Quitter");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Client.exit(true);
            }
        });
        menu.add(exitItem);

        icon = new TrayIcon(new ImageIcon(Tray.class.getResource("/icon.png")).getImage(), "AhChat", menu);
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
