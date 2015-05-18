package me.shadorc.client.frame;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import me.shadorc.client.Client;

public class ClientFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	public static ClientFrame frame;

	public static void main(String[] args) {
		frame = new ClientFrame();
	}

	public ClientFrame() {
		super("Client");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Tray.initialize(this);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(ClientFrame.this.getContentPane().getClass().equals(ConnectedPanel.class)) {
					Client.exit();
				}
			}
		});

		this.setIconImage(new ImageIcon(this.getClass().getResource("/res/icon.png")).getImage());
		this.setContentPane(new ConnectionPanel());
		this.pack();
		this.setMinimumSize(new Dimension(800, 600));
		this.setPreferredSize(new Dimension(800, 600));
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public static void showError(Exception e, String error) {
		JOptionPane.showMessageDialog(null, error, "Client - Erreur", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}

	public static void showError(String error) {
		JOptionPane.showMessageDialog(null, error, "Client - Erreur", JOptionPane.ERROR_MESSAGE);
	}

	public static void setPanel(JPanel pane) {
		frame.setContentPane(pane);
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
	}

	public static Dimension getDimension() {
		return frame.getSize();
	}
}