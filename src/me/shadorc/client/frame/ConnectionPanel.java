package me.shadorc.client.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
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
import me.shadorc.client.Main;
import me.shadorc.client.frame.Button.Size;
import me.shadorc.client.frame.Storage.Data;
import me.shadorc.server.ServerMain;

public class ConnectionPanel extends JPanel implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;

	private JFormattedTextField nameField, ipField;
	private JButton connect, create, iconButton;
	private Image background;
	private File icon;

	public ConnectionPanel() {
		super(new GridBagLayout());
		this.setOpaque(false);

		this.background = new ImageIcon(this.getClass().getResource("/res/background.png")).getImage();

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setPreferredSize(new Dimension(500, 325));
		mainPanel.setOpaque(false);

		icon = new File((Storage.getData(Data.ICON) != null) ? Storage.getData(Data.ICON) : this.getClass().getResource("/res/icon.png").getFile());

		/*Icon Panel*/
		iconButton = new JButton(UserImage.create(icon, 125));
		iconButton.setBorder(BorderFactory.createEmptyBorder());
		iconButton.setHorizontalTextPosition(JButton.CENTER);
		iconButton.setContentAreaFilled(false);
		iconButton.setForeground(Color.RED);
		iconButton.setFocusable(false);
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

		new DropTarget(iconButton, new DropTargetListener() {
			@Override
			public void drop(DropTargetDropEvent event) {
				try {					
					event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

					List <?> files = (List <?>) event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					File file = (File) (files.get(0));

					//Get the type of the file (e.g : image/jpeg")
					String type = new MimetypesFileTypeMap().getContentType(file).split("/")[0];

					if(!type.equals("image")) {
						iconButton.setIcon(UserImage.create(icon, 125));
						return;
					}

					icon = file;
					iconButton.setIcon(UserImage.create(icon, 125));

					Storage.saveData(Data.ICON, icon.getPath());

				} catch (Exception e) {
					iconButton.setIcon(UserImage.create(icon, 125));
					e.printStackTrace();
				}
			}

			@Override
			public void dragEnter(DropTargetDragEvent e) {
				iconButton.setIcon(UserImage.create(new File(this.getClass().getResource("/res/drop_icon.png").getFile()), 125));
			}

			@Override
			public void dragExit(DropTargetEvent e) {
				iconButton.setIcon(UserImage.create(icon, 125));
			}

			@Override
			public void dragOver(DropTargetDragEvent e) { }

			@Override
			public void dropActionChanged(DropTargetDragEvent e) { }
		});

		mainPanel.add(iconButton, BorderLayout.PAGE_START);

		JPanel loginPanel = new JPanel(new GridLayout(2, 2, 20, 25));
		loginPanel.setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));
		loginPanel.setOpaque(false);

		/*Pseudo Panel*/
		JLabel name = new JLabel("Pseudo :", JLabel.RIGHT);
		name.setForeground(Color.BLACK);
		name.setFont(new Font("Segoe UI", Font.PLAIN, 28));
		loginPanel.add(name);

		nameField = new JFormattedTextField(Storage.getData(Data.PSEUDO));
		nameField.addKeyListener(this);
		loginPanel.add(nameField);

		/*IP Panel*/
		JLabel ip = new JLabel("IP du Serveur :", JLabel.RIGHT);
		ip.setForeground(Color.BLACK);
		ip.setFont(new Font("Segoe UI", Font.PLAIN, 28));
		loginPanel.add(ip);

		ipField = new JFormattedTextField(Storage.getData(Data.IP));
		ipField.addKeyListener(this);
		loginPanel.add(ipField);

		mainPanel.add(loginPanel, BorderLayout.CENTER);

		JPanel buttons = new JPanel(new GridLayout(0, 2, 20, 0));
		buttons.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		buttons.setOpaque(false);

		/*Create Chat Room Button Panel*/
		JPanel createPanel = new JPanel(new BorderLayout());
		createPanel.setOpaque(false);
		create = new Button("create", "Créer un salon", Size.NORMAL, this);
		createPanel.add(create, BorderLayout.EAST);
		buttons.add(createPanel);

		/*Connection Button Panel*/
		JPanel connectPanel = new JPanel(new BorderLayout());
		connectPanel.setOpaque(false);
		connect = new Button("validate", "Connexion", Size.NORMAL, this);
		connectPanel.add(connect, BorderLayout.WEST);
		buttons.add(connectPanel);

		mainPanel.add(buttons, BorderLayout.PAGE_END);

		this.add(mainPanel);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JButton bu = (JButton) event.getSource();
		if(bu == iconButton) {
			JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.home"), "Desktop"));
			chooser.setFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
			chooser.setAcceptAllFileFilterUsed(false);

			if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				icon = chooser.getSelectedFile();
				iconButton.setIcon(UserImage.create(icon, 125));
				Storage.saveData(Data.ICON, icon.getPath());
			}

		} else if(bu == connect) {
			this.connection();

		} else if(bu == create) {
			if(!ServerMain.isOpen()) {
				ServerMain.init();
			} else {
				ServerMain.getFrame().setVisible(true);
				ServerMain.getFrame().toFront();
			}
		}
	}

	private void connection() {
		if(nameField.getText().isEmpty() 
				|| ipField.getText().isEmpty() 
				|| !nameField.getText().replaceAll("[^0-9a-zA-Z]", "").equals(nameField.getText())) { //Test if name contains others than letters or number
			Main.showErrorDialog(new Exception("Champs incorrects"), "Merci de remplir tous les champs correctement. (Les pseudos ne peuvent contenir que des lettres et des chiffres)");

		} else {
			connect.setText("Connexion...");
			connect.setEnabled(false);

			new Thread(new Runnable() {
				@Override
				public void run() {
					ConnectedPanel pane = new ConnectedPanel(); //Sinon users est null et il y a une erreur lors du launch
					if(Client.connect(nameField.getText(), icon, ipField.getText())) {
						Main.getFrame().setPanel(pane);
					} else {
						Main.showErrorDialog(new Exception("Serveur indisponible"), "Serveur indisponible ou inexistant.");
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
