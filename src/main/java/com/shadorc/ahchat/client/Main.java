package com.shadorc.ahchat.client;

import com.shadorc.ahchat.client.frame.Frame;
import com.shadorc.ahchat.client.frame.Storage;
import com.shadorc.ahchat.client.frame.Tray;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.io.IOException;

public class Main {

    private static Frame frame;

    public static void main(final String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception err) {
            System.err.println("An error occurred while setting UIMAnager look and feel: " + err.getMessage());
            err.printStackTrace();
        }

        try {
            Storage.init();
        } catch (final IOException err) {
            System.err.println("Cannot create save file, aborting.");
            err.printStackTrace();
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> Main.frame = new Frame());

        Tray.init();
    }

    public static Frame getFrame() {
        return Main.frame;
    }

    public static void showErrorDialog(final Exception e, final String errorMessage) {
        JOptionPane.showMessageDialog(null, errorMessage, "AhChat - Erreur", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
