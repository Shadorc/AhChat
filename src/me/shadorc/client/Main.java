package me.shadorc.client;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import me.shadorc.client.frame.Frame;
import me.shadorc.client.frame.Tray;

public class Main {

	private static Frame frame;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame = new Frame();
			}
		});
		Tray.initialize();
	}

	public static Frame getFrame() {
		return frame;
	}

	public static void showErrorDialog(Exception e, String errorMessage) {
		JOptionPane.showMessageDialog(null, errorMessage, "AhChat - Erreur", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}
}
