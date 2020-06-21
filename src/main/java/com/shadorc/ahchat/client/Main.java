package com.shadorc.ahchat.client;

import com.shadorc.ahchat.client.frame.Frame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    private static Frame frame;

    public static void main(final String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception err) {
            System.err.println("An error occurred while setting UIManager look and feel: " + err.getMessage());
            err.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> Main.frame = new Frame());

        // TODO: Tray.init();
    }

    public static Frame getFrame() {
        return Main.frame;
    }

    public static void showErrorDialog(final Exception e, final String errorMessage) {
        JOptionPane.showMessageDialog(null, errorMessage, "AhChat - Erreur", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
