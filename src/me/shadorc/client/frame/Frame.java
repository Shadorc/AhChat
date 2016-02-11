package me.shadorc.client.frame;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import me.shadorc.client.Client;
import me.shadorc.server.ServerMain;

public class Frame extends JFrame {

	private static final long serialVersionUID = 1L;

	public Frame() {
		super("AhChat");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//Don't exit if Server is launched
				Client.exit(!ServerMain.isOpen());
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

	public void setPanel(JPanel panel) {
		this.setContentPane(panel);
		this.getContentPane().revalidate();
		this.getContentPane().repaint();
	}
}