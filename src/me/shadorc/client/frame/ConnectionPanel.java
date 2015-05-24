package me.shadorc.client.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import me.shadorc.client.Client;
import me.shadorc.server.ServerFrame;

public class ConnectionPanel extends JPanel implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;

	private JFormattedTextField nameField, ipField;
	private JButton connect, create, iconButton;
	private Image background;
	private File icon;

	public ConnectionPanel() {
		super(new GridBagLayout());

		this.background = new ImageIcon(this.getClass().getResource("/res/background.jpg")).getImage();

		JPanel mainPanel = new JPanel(new GridLayout(2, 0));
		mainPanel.setOpaque(false);

		icon = new File(this.getClass().getResource("/res/icon.png").getFile());

		/*Icon Panel*/
		Box box = new Box(BoxLayout.X_AXIS);

		iconButton = new JButton(UserImage.create(icon));
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

		box.add(Box.createHorizontalGlue());
		box.add(iconButton);
		box.add(Box.createHorizontalGlue());
		mainPanel.add(box);

		JPanel loginPanel = new JPanel(new GridLayout(4, 0));
		loginPanel.setOpaque(false);

		/*Pseudo Panel*/
		JPanel pseudoPane = new JPanel(new BorderLayout());
		pseudoPane.setOpaque(false);
		JLabel name = new JLabel("Pseudo :");
		name.setForeground(Color.BLACK);
		name.setPreferredSize(new Dimension(100, 30));
		pseudoPane.add(name, BorderLayout.WEST);

		nameField = new JFormattedTextField();
		nameField.addKeyListener(this);
		pseudoPane.add(nameField, BorderLayout.CENTER);

		loginPanel.add(pseudoPane);

		/*IP Panel*/
		JPanel ipPane = new JPanel(new BorderLayout());
		ipPane.setOpaque(false);
		JLabel ip = new JLabel("IP du Serveur :");
		ip.setForeground(Color.BLACK);
		ip.setPreferredSize(new Dimension(100, 30));
		ipPane.add(ip, BorderLayout.WEST);

		ipField = new JFormattedTextField();
		ipField.addKeyListener(this);
		ipPane.add(ipField, BorderLayout.CENTER);
		loginPanel.add(ipPane);

		/*Connexion Button Panel*/
		JPanel connectionPane = new JPanel(new GridLayout(0, 3));
		connectionPane.setOpaque(false);
		connectionPane.add(new JLabel());
		connect = new JButton("Connexion");
		connect.setBackground(Color.WHITE);
		connect.addActionListener(this);
		connect.setFocusable(false);
		connectionPane.add(connect);
		connectionPane.add(new JLabel());
		loginPanel.add(connectionPane);

		/*Créer Salon Button Panel*/
		JPanel createPane = new JPanel(new GridLayout(0, 3));
		createPane.setOpaque(false);
		createPane.add(new JLabel());
		create = new JButton("Créer un salon");
		create.setBackground(Color.WHITE);
		create.addActionListener(this);
		create.setFocusable(false);
		createPane.add(create);
		createPane.add(new JLabel());
		loginPanel.add(createPane);

		loginPanel.setMaximumSize(new Dimension(400, 200));

		mainPanel.add(loginPanel);

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
