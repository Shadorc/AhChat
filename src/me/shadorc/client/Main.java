package me.shadorc.client;

import me.shadorc.client.frame.Frame;
import me.shadorc.client.frame.Storage;
import me.shadorc.client.frame.Tray;

import javax.swing.*;
import java.io.IOException;

public class Main {

    private static Frame frame;

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Storage.init();
        } catch (IOException e) {
            System.err.println("[ERROR] Can not create save file, aborting.");
            e.printStackTrace();
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> frame = new Frame());

        Tray.init();
    }

    public static Frame getFrame() {
        return frame;
    }

    public static void showErrorDialog(Exception e, String errorMessage) {
        JOptionPane.showMessageDialog(null, errorMessage, "AhChat - Erreur", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
