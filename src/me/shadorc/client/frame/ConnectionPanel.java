package me.shadorc.client.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import me.shadorc.client.Client;
import me.shadorc.client.frame.Storage.Data;
import me.shadorc.server.ServerFrame;

public class ConnectionPanel extends JPanel implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;

	private JFormattedTextField nameField, ipField;
	private JButton connect, create, iconButton;
	private Image background;
	private File icon;

	public ConnectionPanel() {
		super(new GridBagLayout());

		this.background = new ImageIcon(this.getClass().getResource("/res/background.png")).getImage();

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setPreferredSize(new Dimension(500, 325));
		mainPanel.setOpaque(false);

		if(Storage.get(Data.ICON) != null) {
			icon = new File(Storage.get(Data.ICON));
		} else {
			icon = new File(this.getClass().getResource("/res/icon.png").getFile());
		}

		/*Icon Panel*/
		iconButton = new JButton(UserImage.create(icon, -1, 125));
		iconButton.addActionListener(this);
		iconButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				iconButton.setText("");
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				iconButton.setText("Changer");
			}
		});
		iconButton.setFocusable(false);
		iconButton.setContentAreaFilled(false);
		iconButton.setBorder(BorderFactory.createEmptyBorder());
		iconButton.setHorizontalTextPosition(JButton.CENTER);
		iconButton.setVerticalTextPosition(JButton.CENTER);
		iconButton.setForeground(Color.RED);
		iconButton.setBackground(Color.WHITE);
		iconButton.setOpaque(false);

		mainPanel.add(iconButton, BorderLayout.PAGE_START);

		JPanel loginPanel = new JPanel(new GridLayout(2, 2, 20, 30));
		loginPanel.setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));
		loginPanel.setOpaque(false);

		/*Pseudo Panel*/
		JLabel name = new JLabel("Pseudo :", JLabel.RIGHT);
		name.setForeground(Color.BLACK);
		name.setFont(new Font("Segoe UI", Font.PLAIN, 28));
		loginPanel.add(name);

		nameField = new JFormattedTextField(Storage.get(Data.PSEUDO));
		nameField.addKeyListener(this);
		loginPanel.add(nameField);

		/*IP Panel*/
		JLabel ip = new JLabel("IP du Serveur :", JLabel.RIGHT);
		ip.setForeground(Color.BLACK);
		ip.setFont(new Font("Segoe UI", Font.PLAIN, 28));
		loginPanel.add(ip);

		ipField = new JFormattedTextField(Storage.get(Data.IP));
		ipField.addKeyListener(this);
		loginPanel.add(ipField);

		mainPanel.add(loginPanel, BorderLayout.CENTER);

		JPanel buttons = new JPanel(new GridLayout(0, 2, 20, 0));
		buttons.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		buttons.setOpaque(false);

		/*Créer Salon Button Panel*/
		JPanel createPanel = new JPanel(new BorderLayout());
		createPanel.setOpaque(false);
		create = new Button("Creer", "Créer un salon", this);
		createPanel.add(create, BorderLayout.EAST);
		buttons.add(createPanel);

		/*Connexion Button Panel*/
		JPanel connectPanel = new JPanel(new BorderLayout());
		connectPanel.setOpaque(false);
		connect = new Button("Valider", "Connexion", this);
		connectPanel.add(connect, BorderLayout.WEST);
		buttons.add(connectPanel);

		mainPanel.add(buttons, BorderLayout.PAGE_END);

		this.setOpaque(false);
		this.add(mainPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton bu = (JButton) e.getSource();
		if(bu == iconButton) {
			JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.home"), "Desktop"));
			chooser.setFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
			chooser.setAcceptAllFileFilterUsed(false);

			int choice = chooser.showOpenDialog(null);

			if(choice == JFileChooser.APPROVE_OPTION) {
				icon = chooser.getSelectedFile();
				iconButton.setIcon(UserImage.create(icon, -1, iconButton.getHeight()));
			}
		} else if(bu == connect) {
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
			connect.setText("Connexion...");
			connect.setEnabled(false);

			new Thread(new Runnable() {
				@Override
				public void run() {
					ConnectedPanel pane = new ConnectedPanel(); //Sinon users est null et il y a une erreur lors du launch
					if(Client.connect(nameField.getText(), icon, ipField.getText())) {
						Frame.setPanel(pane);
					} else {
						Frame.showError("Serveur indisponible ou inexistant.");
						connect.setText("Connexion");
						connect.setEnabled(true);
					}
				}
			}).start();
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
