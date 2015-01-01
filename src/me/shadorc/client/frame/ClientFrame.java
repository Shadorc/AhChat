package me.shadorc.client.frame;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import me.shadorc.client.Client;

class ClientFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new ClientFrame();
	}

	public ClientFrame() {
		super("Client");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		new Tray(this);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(ClientFrame.this.getContentPane().getClass().equals(ConnectedPanel.class)) {
					Client.exit();
				}
			}
		});

		this.setIconImage(new ImageIcon(this.getClass().getResource("/res/icone.png")).getImage());
		this.setContentPane(new ConnectionPanel(this));
		this.pack();
		this.setMinimumSize(new Dimension(800, 600));
		this.setPreferredSize(new Dimension(800, 600));
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
}