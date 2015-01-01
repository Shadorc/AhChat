package me.shadorc.client.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import me.shadorc.client.Client;
import me.shadorc.server.ServerFrame;

class ConnectionPanel extends Box implements ActionListener {

	private static final long serialVersionUID = 1L;

	private final ImageIcon image = new ImageIcon(this.getClass().getResource("/res/fond.jpg"));

	private JFormattedTextField saisisName;
	private JFormattedTextField saisisIp;

	private JButton ok;
	private JButton create;

	private JFrame frame;

	protected ConnectionPanel(JFrame frame) {

		super(BoxLayout.Y_AXIS);

		this.frame = frame;

		JPanel mainPane = new JPanel(new GridLayout(4, 0));
		mainPane.setOpaque(false);

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

		saisisName = new JFormattedTextField();
		top.add(saisisName, BorderLayout.CENTER);

		JLabel ip = new JLabel("IP du Serveur :");
		ip.setForeground(Color.BLACK);
		ip.setPreferredSize(new Dimension(100, 30));
		center.add(ip, BorderLayout.WEST);

		saisisIp = new JFormattedTextField();
		center.add(saisisIp, BorderLayout.CENTER);

		bottom.add(new JLabel());
		ok = new JButton("Connexion");
		ok.setBackground(Color.WHITE);
		ok.addActionListener(this);
		bottom.add(ok);
		bottom.add(new JLabel());

		bottom2.add(new JLabel());
		create = new JButton("Créer un salon");
		create.setBackground(Color.WHITE);
		create.addActionListener(this);
		bottom2.add(create);
		bottom2.add(new JLabel());

		mainPane.add(top);
		mainPane.add(center);
		mainPane.add(bottom);
		mainPane.add(bottom2);

		mainPane.setMaximumSize(new Dimension(400, 100));

		this.setOpaque(false);
		this.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.add(Box.createVerticalGlue());
		this.add(mainPane);
		this.add(Box.createVerticalGlue());
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		JButton bu = (JButton) e.getSource();

		if(bu == ok) {
			if(saisisName.getText().isEmpty() 
					|| saisisIp.getText().isEmpty() 
					|| !saisisIp.getText().matches("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$")  //Test if the IP address contains letters
					|| !saisisName.getText().replaceAll("[^0-9a-zA-Z]", "").equals(saisisName.getText())) { //Test if name contains others than letters or number
				JOptionPane.showMessageDialog(null, "Merci de remplir tous les champs correctement. (Les pseudos ne peuvent contenir que des lettres et des chiffres)", "Erreur", JOptionPane.ERROR_MESSAGE);
			} else {
				ConnectedPanel pane = new ConnectedPanel(frame); //Sinon users est null et il y a une erreur lors du launch
				if(Client.launch(saisisName.getText(), saisisIp.getText())) {
					frame.setContentPane(pane);
					frame.revalidate();
				} else {
					JOptionPane.showMessageDialog(null, "Serveur indisponible ou inexistant.", "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			}

		} else if(bu == create) {
			new ServerFrame();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image.getImage(), 0, 0, this.getWidth(), this.getHeight(), 0, 0, image.getIconWidth(), image.getIconHeight(), this);
	}
}
