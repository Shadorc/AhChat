package me.shadorc.client;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import me.shadorc.client.frame.Frame;
import me.shadorc.client.frame.Tray;

public class Main {

	private static Frame frame;

	public static void main(String[] args) {

		//Change UIManager look to look like the operating system one
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

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
