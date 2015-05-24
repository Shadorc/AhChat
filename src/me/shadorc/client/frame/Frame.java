package me.shadorc.client.frame;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import me.shadorc.client.Client;
import me.shadorc.server.ServerFrame;

public class Frame extends JFrame {

	private static final long serialVersionUID = 1L;

	private static boolean isOpen = false;

	public static Frame frame;

	public static void main(String[] args) {
		frame = new Frame();
	}

	public Frame() {
		super("AhChat");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		isOpen = true;

		Tray.initialize(this);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				isOpen = false;
				//Don't exit if Server is launched
				Client.exit(!ServerFrame.isOpen());
			}
		});

		this.setIconImage(new ImageIcon(this.getClass().getResource("/res/icon.png")).getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
		this.setContentPane(new ConnectionPanel());
		this.pack();
		this.setMinimumSize(new Dimension(800, 600));
		this.setPreferredSize(new Dimension(800, 600));
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public static void popupError(Exception e, String error) {
		JOptionPane.showMessageDialog(null, error, "AhChat - Erreur", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}

	public static void popupError(String error) {
		JOptionPane.showMessageDialog(null, error, "AhChat - Erreur", JOptionPane.ERROR_MESSAGE);
	}

	public static void setPanel(JPanel pane) {
		frame.setContentPane(pane);
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
	}

	public static Dimension getDimension() {
		return frame.getSize();
	}

	public static boolean isOpen() {
		return isOpen;
	}
}