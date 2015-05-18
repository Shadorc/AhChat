package me.shadorc.client.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import me.shadorc.client.Client;
import me.shadorc.server.ServerFrame;

public class ConnectionPanel extends Box implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;

	private JFormattedTextField nameField, ipField;
	private JButton connect, create;
	private Image background;

	public ConnectionPanel() {

		super(BoxLayout.Y_AXIS);

		this.background = new ImageIcon(this.getClass().getResource("/res/background.jpg")).getImage();

		JPanel panel = new JPanel(new GridLayout(4, 0));
		panel.setOpaque(false);

		JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(false);
		JPanel center = new JPanel(new BorderLayout());
		center.setOpaque(false);
		JPanel bottom = new JPanel(new GridLayout(0, 3));
		bottom.setOpaque(false);
		JPanel bottom2 = new JPanel(new GridLayout(0, 3));
		bottom2.setOpaque(false);

		JLabel name = new JLabel("Pseudo :");
		name.setForeground(Color.BLACK);
		name.setPreferredSize(new Dimension(100, 30));
		top.add(name, BorderLayout.WEST);

		nameField = new JFormattedTextField();
		nameField.addKeyListener(this);
		top.add(nameField, BorderLayout.CENTER);

		JLabel ip = new JLabel("IP du Serveur :");
		ip.setForeground(Color.BLACK);
		ip.setPreferredSize(new Dimension(100, 30));
		center.add(ip, BorderLayout.WEST);

		ipField = new JFormattedTextField();
		ipField.addKeyListener(this);
		center.add(ipField, BorderLayout.CENTER);

		bottom.add(new JLabel());
		connect = new JButton("Connexion");
		connect.setBackground(Color.WHITE);
		connect.addActionListener(this);
		bottom.add(connect);
		bottom.add(new JLabel());

		bottom2.add(new JLabel());
		create = new JButton("Créer un salon");
		create.setBackground(Color.WHITE);
		create.addActionListener(this);
		bottom2.add(create);
		bottom2.add(new JLabel());

		panel.add(top);
		panel.add(center);
		panel.add(bottom);
		panel.add(bottom2);

		panel.setMaximumSize(new Dimension(400, 100));

		this.setOpaque(false);
		this.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.add(Box.createVerticalGlue());
		this.add(panel);
		this.add(Box.createVerticalGlue());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton bu = (JButton) e.getSource();

		if(bu == connect) {
			this.connection();
		} else if(bu == create) {
			new ServerFrame();
		}
	}

	private void connection() {
		if(nameField.getText().isEmpty() 
				|| ipField.getText().isEmpty() 
				|| !ipField.getText().matches("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$")  //Test if the IP address contains letters
				|| !nameField.getText().replaceAll("[^0-9a-zA-Z]", "").equals(nameField.getText())) { //Test if name contains others than letters or number
			Frame.showError("Merci de remplir tous les champs correctement. (Les pseudos ne peuvent contenir que des lettres et des chiffres)");

		} else {
			ConnectedPanel pane = new ConnectedPanel(); //Sinon users est null et il y a une erreur lors du launch
			if(Client.connect(nameField.getText(), ipField.getText())) {
				Frame.setPanel(pane);
			} else {
				Frame.showError("Serveur indisponible ou inexistant.");
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), 0, 0, background.getWidth(null), background.getHeight(null), this);
	}

	@Override
	public void keyPressed(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.VK_ENTER) {
			this.connection();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) { }

	@Override
	public void keyTyped(KeyEvent arg0) { }
}
